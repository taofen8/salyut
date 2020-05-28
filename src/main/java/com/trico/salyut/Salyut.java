/*
 * Copyright (c) 2018 tirco.cloud. All rights reserved.
 *
 * This source code is licensed under Apache 2.0 License,
 * attached with Common Clause Condition 1.0, found named CC-1.0.txt.
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */

package com.trico.salyut;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.trico.salyut.browser.SBrowser;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.log.Log;
import com.trico.salyut.log.Message;
import com.trico.salyut.struct.TricoScript;
import com.trico.salyut.utils.Md5Encrypt;
import com.trico.salyut.token.Segment;

/**
 * Salyut为引擎静态启动类
 * <p>Salyut负责来创建一个或者多个{@link SBrowser}实例，接收trico脚本代码并且传递给SBrowser来处理.
 *
 * <pre>
 * {@code
 *  Salyut.setEnv(Salyut.EnvKey.RECREATE_UPPER_LIMIT,Integer.MAX_VALUE);
 *  Salyut.launch();
 *
 *  Salyut.setOutputListener(
 *  	msg -> {
 *  		//引擎处理过程中产生的消息
 *   	}
 *  );
 *  Salyut.setResultListener(
 *   	result -> {
 *   		//返回结果
 *  	}
 *  );
 * }
 * </pre>
 *
 * @author Shen Yin
 */
public class Salyut
{
	/**
	 * 限制{@link SBrowser} 实例数
	 */
	private static int LIMITED  = 1;
	/** 实例存放数组 */
	private static final ArrayList<SBrowser> BROWSERS;

	/**
	 * 存储Salyut所收到的trico脚本
	 * @see #execScript(String, String, String)
	 */
	private static final ConcurrentLinkedQueue<Salyut.ScriptStruct> scriptQueue;

	// ------------------------------------------------------------------------
	//  Salyut监听器
	// ------------------------------------------------------------------------

	public interface ResultListener{  void get(Map<String,Object> map); }
	public interface OutputListener{ void present(Message message); }

	/** 消息监听器 */
	private static OutputListener outputListener;
	public static void setOutputListener(OutputListener listener){ outputListener = listener; }
	public static OutputListener getOutputListener(){
		return outputListener;
	}

	/** 结果监听器 */
	private static ResultListener resultListener;
	public static void setResultListener(ResultListener listener){
		resultListener =listener;
	}
	public static ResultListener getResultListener(){
		return resultListener;
	}

	/**
	 * Salyut引擎启动的后，所有被加载的segment将会放入segmentMap
	 * Key为 {@link Segment}的seg参数
	 */
	public static final ConcurrentHashMap<String,String>  segmentMap;

	/**
	 * Salyut所需的环境变量存储器
	 * Key为 {@link EnvKey}
	 */
	private static final Map<EnvKey,Object> envMap = new HashMap<>();

	/** EnvKey枚举类
	 * code 唯一标识符
	 * propertyName jvm参数名
	 * requiredType EnvKey所对应的值的类型*/
	public enum EnvKey{
		/** geckodriver 路径 必须存在否则SBrowser无法启动 */
		DRIVER_PATH(0,"driver.path",String.class),

		/** salyut自带的网页，用来新建tab。必须存在否则SBrowser无法启动 */
		NEW_TAB_PATH(1,"newTab.path",String.class),

		/** salyut启动时，默认加载的segment路径，可为空 */
		SEGMENT_PATH(2,"segment.path", String.class),

		/** 运行脚本时是否使用无框浏览器，默认为false */
		HEADLESS(3,"headless",Boolean.class),

		/**
		 * 启动的{@link SBrowser}个数，默认为1
		 */
		BROWSER_COUNT(4,"browser.count",Integer.class),

		/** 浏览器类型，目前只支持firefox，默认为firefox */
		BROWSER_TYPE(5,"browser.type",String.class),

		/** 运行的脚本路径 可为空 */
		SCRIPT_PATH(6,"script.path",String.class),

		/** 脚本运行多少次重新创建driver，仅当您需要通过salyut部署您自己的线上服务时候需要设置
		 * 定期的重新创建driver可以提高salyut本身的稳定性，并且及时释放内存。默认为Integer.MAX*/
		RECREATE_UPPER_LIMIT(7,"recreate.upperlimit",Integer.class);


		int code;
		String propertyName;
		Class requiredType;
		EnvKey(int code,String propertyName, Class requiredType){
			this.code = code;
			this.propertyName = propertyName;
			this.requiredType = requiredType;
		}

		public String getPropertyName(){
			return propertyName;
		}

		public Class getRequiredType(){
			return requiredType;
		}

		public boolean equals(EnvKey other){
			return code == other.code;
		}
	}

	/** Salyut结果数据结构
	 * Salyut真正的处理结果存放在data中，为json字符串*/
	public static class Result{
		Map<String,Object> map;

		private Result(Builder builder){
			this.map = new HashMap<>();
			map.put("succeed",builder.succeed);
			map.put("provider",builder.provider);
			map.put("job_id",builder.jobId);
			map.put("data",builder.data);
		}

		public static class Builder{
			boolean succeed;
			String provider = "Provider by trico.";
			String jobId;
			Object data;

			public Builder succeed(boolean succeed){
				this.succeed = succeed;
				return this;
			}

			public Builder jobId(String jobId){
				this.jobId = jobId;
				return this;
			}

			public Builder data(Object data){
				this.data = data;
				return this;
			}

			public Result build(){
				return new Result(this);
			}
		}

		public Map<String,Object> get(){
			return map;
		}
	}

	// ------------------------------------------------------------------------


	/**
	 * setEnv 设置Salyut的环境变量，同时会检查value的类型
	 * @see EnvKey#requiredType
	 * @param key
	 * 		{@link EnvKey}
	 * @param value
	 * 		环境变量值
	 * @throws SalyutException salyut异常
	 */
	public static void setEnv(EnvKey key,Object value) throws SalyutException {
		if (!value.getClass().equals(key.getRequiredType())){
			throw new SalyutException(SalyutExceptionType.RuntimeError,
					String.format("Env key %s expect %s type",key.getPropertyName(),key.getRequiredType()));
		}

		if (EnvKey.NEW_TAB_PATH.equals(key)){
			value = "file://"+value;
		}

		envMap.put(key,value);
	}

	/**
	 * 支持先从JVM参数中获取相应的值，如果在JVM参数中未获取到，
	 * 将从envMap中获取
	 *
	 * @see EnvKey#propertyName
	 * @param key
	 * 		{@link EnvKey}
	 *
	 * @return env key所对应的值
	 */
	public static Object getEnv(EnvKey key){
		String tmp = System.getProperty(key.getPropertyName());
		if (tmp != null){
			if (EnvKey.NEW_TAB_PATH.equals(key)){
				if (!tmp.startsWith("file://")){
					return "file://"+tmp;
				}
			}
			else if (key.getRequiredType().equals(Boolean.class)){
				return "true".equals(tmp);
			}
			else if (key.getRequiredType().equals(Integer.class)){
				return Integer.parseInt(tmp);
			}
		}
		else{
			return envMap.get(key);
		}

		return tmp;
	}

	static{
		scriptQueue = new ConcurrentLinkedQueue<>();
		segmentMap = new ConcurrentHashMap<>();
		BROWSERS = new ArrayList<>();

		/*env default setting*/
		envMap.put(EnvKey.BROWSER_COUNT,1);
		envMap.put(EnvKey.HEADLESS, true);
		envMap.put(EnvKey.BROWSER_TYPE,"firefox");
		envMap.put(EnvKey.RECREATE_UPPER_LIMIT,Integer.MAX_VALUE);
	}

	public static void stopJob(String jobId){
		for (SBrowser browser:BROWSERS){
			if (browser.stopJob(jobId)){
				return;
			}
		}
	}


	/**
	 * 从scriptQueue中获取待处理脚本
	 * @return 待处理脚本 {@link ScriptStruct}
	 */
	public static ScriptStruct pollScript(){
		return scriptQueue.poll();
	}

	/**
	 * 向scriptQueue追加脚本
	 */
	private static void offerScript(ScriptStruct scriptStruct){
		scriptQueue.offer(scriptStruct);
	}

	
	public static synchronized void putSegment(String key,String segmentYaml){
		String storedSegYaml = segmentMap.get(key);
		if (storedSegYaml == null || !Md5Encrypt.md5(storedSegYaml, "utf-8").equals(Md5Encrypt.md5(segmentYaml, "utf-8"))){
			segmentMap.put(key, segmentYaml);
		}
	}

	/**
	 * 预读取segmentPath在的.tr文件
	 * @param segmentPath
	 * 		segment文件路径
	 * @throws IOException io异常
	 */
	public static void loadSegs(String segmentPath) throws  IOException{
		if (null == segmentPath){
			return;
		}
		File segs = new File(segmentPath);
		File[] files = segs.listFiles();
		if (null == files){
			return;
		}
		for (File seg:files){
			if (null == seg || seg.getName().startsWith(".")){
				continue;
			}
			if (seg.canRead()){
				StringBuilder builder = new StringBuilder();
				FileReader reader = new FileReader(seg);
				try(BufferedReader br = new BufferedReader(reader)){
					String line;
					while ((line = br.readLine()) != null) {
						builder.append(line);
						builder.append("\n");
					}

					TricoScript s = new TricoScript(builder.toString());
					STab.processSegmentFile(s.getContent());
				}catch (Exception e){
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * 启动Salyut，
	 * 1.指定browser实例个数
	 * 2.加载segment路径下的seg的文件
	 */
	public static void launch(){
		try {
			Salyut.LIMITED = (int)getEnv(EnvKey.BROWSER_COUNT);
			loadSegs((String) getEnv(EnvKey.SEGMENT_PATH));
		} catch (Exception e) {
			e.printStackTrace();
			Log.logger.error(e);
		}
    }

	/**
	 * 执行trico脚本。{@link SBrowser}为懒加载，当真正有脚本执行时才会初始化
	 * 当BROWSER_COUNT 大于 1 时 Salyut将会选取空闲的Browser执行脚本任务
	 * @param script
	 * 		  trico脚本
	 * @param channelId
	 * 		  web socket通道id
	 * @param missionId
	 * 		  任务id
	 * @throws SalyutException salyut异常
	 */
	public static synchronized void execScript(String script,String channelId,String missionId) throws SalyutException{
		if (BROWSERS.size() < Salyut.LIMITED){
			SBrowser browser = SBrowser.ofNonPlugin();
			browser.newTab(1);
	    	browser.start();
			BROWSERS.add(browser);
		}
		for (SBrowser browser:BROWSERS){
			if (browser.isIdle()){
				browser.open(script,channelId,missionId);
				return;
			}
		}
		Log.logger.info("no idle browsers, offer 2 queue, mission id:"+missionId);
		offerScript(new ScriptStruct.Builder().script(script).channelId(channelId).missionId(missionId).build());
	}
	


	public static class ScriptStruct{
		public final String script;
		private String channelId;
		private String missionId;

		ScriptStruct(Builder builder){
			this.script = builder.script;
			this.channelId = builder.channelId;
			this.missionId = builder.missionId;
		}

		public String getScript() {
			return script;
		}

		public String getChannelId() {
			return channelId;
		}

		public String getMissionId() {
			return missionId;
		}

		public static class Builder{
			String script;
			String channelId;
			String missionId;

			public Builder script(String script){
				this.script = script;
				return this;
			}

			public Builder channelId(String channelId){
				this.channelId = channelId;
				return this;
			}

			public Builder missionId(String missionId){
				this.missionId = missionId;
				return this;
			}

			public ScriptStruct build(){
				return new ScriptStruct(this);
			}
		}

	}
	
	
    public static void main( String[] args ) throws SalyutException
    {
    	String driverPath = "/Users/ris/geckodriver";
    	String newTabPath = "/Users/ris/newTab.html";

		Salyut.setEnv(EnvKey.DRIVER_PATH,driverPath);
		Salyut.setEnv(EnvKey.NEW_TAB_PATH,newTabPath);
		Salyut.setEnv(EnvKey.HEADLESS,false);

		Salyut.launch();
		Salyut.setResultListener( result -> {
			System.out.println(JSON.toJSONString(result, SerializerFeature.PrettyFormat));
			System.exit(0);
		});

		String yaml;

		if (Salyut.getEnv(EnvKey.SCRIPT_PATH) != null){
			File file = new File((String) Salyut.getEnv(EnvKey.SCRIPT_PATH));
			Long fileLength = file.length();
			byte[] fileContent = new byte[fileLength.intValue()];
			try(FileInputStream in = new FileInputStream(file)){
				int flag = in.read(fileContent);

			}catch (Exception e){
				e.printStackTrace();
			}

			yaml = new String(fileContent, StandardCharsets.UTF_8);
		}
		else{
			throw new SalyutException(SalyutExceptionType.RuntimeError,"Can not find trico script,please check if you set the -Dscript.path=");
		}


		TricoScript script = new TricoScript(yaml);

		Salyut.execScript(script.getContent(),"","");
    }


}
