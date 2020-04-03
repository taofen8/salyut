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

package com.trico.salyut.token;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.engine.Statements;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.utils.MapUtil;
import com.trico.salyut.yaml.SYaml;
import org.yaml.snakeyaml.Yaml;
import com.trico.salyut.STab;
import com.trico.salyut.Salyut;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.path.Path;

/**
 * <b>segment</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/segment/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "segment")
public class Segment extends SToken {
	private String name;
	private String packageName = "default";
	private List<String> args = new ArrayList<String>();
	private List<String> simpleArgs = new ArrayList<String>();
	private static AtomicLong autoIncrease = new AtomicLong(0);
	private Long idf = null;

	
	String getSegmentPath(){
		return "/"+Path.INNER_VAR_PREFIX+"seg/"+this.packageName+"_"+this.name+"/"+this.idf;
	}

	@Override
	protected void parseParam(Object param) throws SalyutException{
		super.parseParam(param);
		this.idf = autoIncrease.incrementAndGet();
		Map<String, Object> map = (Map<String, Object>) param;
		List<Map<String,Object>> body = null;

		Map<String, Object> sortedMap = MapUtil.sortByKey(map,true);

		Iterator<String> it = sortedMap.keySet().iterator();

		while(it.hasNext()){
			String key = it.next();
			if ("name".equals(cleanKey(key))){
				try {
					this.name = ((String) getExprValue((String) map.get(key)));
				}catch (SalyutException e){
					throw new SalyutException(e.getType(),this,e.getAddonMessage().replace("?"," of name"));
				}

			}
			else if ("package".equals(cleanKey(key))){
				try {
					this.packageName = ((String) getExprValue((String) map.get(key)));
				}catch (SalyutException e){
					throw new SalyutException(e.getType(),this,e.getAddonMessage().replace("?", " of package"));
				}

			}
			else if ("body".equals(cleanKey(key))){
				body = (List<Map<String,Object>>) map.get(key);
			}
			else if ("args".equals(cleanKey(key))){
				Map<String,Object> argsMap = (Map<String, Object>) map.get(key);
				Integer argIndex = 0;
				while(argIndex < 50){
					String argPath;
					if ((argPath = (String) argsMap.get(String.valueOf(argIndex))) == null){
						if((argPath = (String) argsMap.get(argIndex)) == null){
							break;
						}

					}
					this.args.add(getSegmentPath()+argPath);
					this.simpleArgs.add(argPath);
					argIndex++;
				}
			}
		}

		//如果是atTab为null只是创建segment Token，不需要马上创建statements。
		if (atTab != null){
			this.statements = Statements.of(SToken.getList(body, atTab,getLevel()+1,this));
		}


		Salyut.putSegment(getName(),yamlString());
	}

	@Override
	public void action() throws SalyutException {
		//called by #invoke
	}
	

	
	public String getName(){
		return packageName+"_"+name;
	}

	public String getSimpleName(){
		return name;
	}

	public String getPackageName(){
		return packageName;
	}
	
	public List<String> getArgs(){
		return args;
	}

	public List<String> getSimpleArgs(){
		return simpleArgs;
	}

	public String getPureCode() throws SalyutException{
		List<LinkedHashMap<String,Object>> list = SYaml.toObj(yamlString);
		return SYaml.toPureCode(list.get(0));
	}

	@SuppressWarnings("unchecked")
	public static Segment produceSelf(String yamlString,STab tab) throws SalyutException{
		Yaml yaml = new Yaml();
		Map<String,Object> yamlObject;
		try {
			yamlObject = ((List<Map<String,Object>>)yaml.load(yamlString)).iterator().next();
		}
		catch (Exception e){
			throw new SalyutException(SalyutExceptionType.RuntimeError,"segment load error");
		}

		String key = yamlObject.keySet().iterator().next();
		String tokenName = key;
		String line = null;
		if (key.contains("_")){
			String[] keyAndLine = key.split("_");
			tokenName = keyAndLine[0];
			line = keyAndLine[1];
		}

		Integer lineNo = line != null ? Integer.parseInt(line):null;
		if (SToken.isTargetTok(tokenName,Segment.class)){
			return  SToken.ofClz(Segment.class,yamlObject.get(key),tab,0L,lineNo,null,yamlString);
		}
		else{
			throw new SalyutException(SalyutExceptionType.RuntimeError,"segment load error");
		}
	}

	/**
	 * 调用 {@link Segment}代码片段
	 */
	public ExecResult invoke() throws SalyutException {
		return atTab.executor.execute(statements);
	}



}
