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

import java.util.*;

import com.trico.salyut.token.*;
import com.trico.salyut.path.Path;
import com.trico.salyut.yaml.SYaml;
import com.trico.salyut.token.SToken;
import com.trico.salyut.log.MessageTag;
import com.trico.salyut.browser.SBrowser;
import com.trico.salyut.engine.ExecUnit;
import com.trico.salyut.engine.Executor;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.engine.Statements;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;

import org.openqa.selenium.remote.RemoteWebDriver;
import org.reflections.Reflections;

/**
 * 浏览器Tab对象，包含当前yaml文件所要执行的Token List
 *
 * @author shenyin
 */
public class STab implements ExecUnit {
	/**
	 * tab状态 初始值为 IDLE
	 * IDLE tab空闲，可接受脚本任务
	 * READY 脚本解析完成，待执行
	 * BUSY 繁忙，脚本正在执行
	 *
	 * 由于{@link RemoteWebDriver}的操作对浏览器是阻塞操作，故目前只会生成一个TAB
	 * 考虑后期再做优化
	 */
	private volatile int state = IDLE;

	private static final int IDLE = 0;
	private static final int READY = 1;
	private static final int BUSY = 2;

	private void idle(){ this.state = IDLE; }
	private void ready(){ this.state = READY; }
	private void busy(){ this.state = BUSY; }

	@Override
	public boolean isIdle(){ return IDLE == state; }

	@Override
	public boolean isBusy() { return BUSY == state; }

	@Override
	public String jobId() {
		return job.id;
	}

	@Override
	public boolean isReady(){ return READY == state; }


	public RemoteWebDriver driver;

	/** tab所需要执行的job */
	private Job job;

	public final Executor<SToken> executor = new Executor<>();
	/** SBrowser引用 */
    private SBrowser browser;

    private volatile boolean stop;

	public final Map<String, Class<? extends SToken>> tokenClzMap = new HashMap<>();

	@SuppressWarnings("unchecked")
	private void buildAvailableTokenMap(){
		Reflections reflections = new Reflections("com.trico.salyut.token");
		Set<Class<?>> tokens = reflections.getTypesAnnotatedWith(TokenMark.class);
		for (Class<?> token:tokens){
			TokenMark tokenAnno = token.getAnnotation(TokenMark.class);
			tokenClzMap.put(tokenAnno.name(),(Class<? extends SToken>)token);
		}
	}

	@Override
	public ExecResult execute() throws SalyutException {
		return load();
	}

	public static class Job{
		public final String id;
		/** trico脚本执行语句 */
		private final Statements<SToken> statements;
		/** Salyut服务基于web socket封装后可透传的通道表示标识 */
		public final String channelId;

		public final String script;
		/**
		 * 用于存储Salyut引擎的上下文数据
		 * 数据结构为HashMap
		 * 单任务生命周期
		 */
		public final Path path = new Path();

		Job(Builder builder){
			this.id = builder.id;
			this.statements = builder.statements;
			this.channelId = builder.channelId;
			this.script = builder.script;
		}

		public static class Builder{
			String id;
			Statements<SToken> statements;
			String channelId;
			String script;

			public Builder id(String id){
				this.id = id;
				return this;
			}

			public Builder statements(Statements<SToken> statements){
				this.statements = statements;
				return this;
			}

			public Builder channelId(String channelId){
				this.channelId = channelId;
				return this;
			}

			public Builder script(String script){
				this.script = script;
				return this;
			}

			public Job build(){
				return new Job(this);
			}
		}
	}

	public STab(RemoteWebDriver driver){
		this.driver = driver;
		buildAvailableTokenMap();
	}

	@Override
	public void reset(){
		offerMessage("[proto]:{\"scriptState\":\"finished\",\"missionId\":\""+job.id+"\"}");
		offerMessage("[engine]: script "+ (stop ? "canceled.":"completed."));
		this.job = null;
		idle();
		this.stop = false;

	}

	public boolean hasTargetJob(String jobId){
		if (null == jobId){
			return false;
		}
		return job.id.equals(jobId);
	}

	public void stop(){
		this.stop = true;
	}

	public boolean needStop(){
		return stop;
	}

	/**
	 * open
	 *
	 * 打开job {@link STab#state} = {@link STab#READY}
	 * job不会真正执行，需要调用{@link STab#load()}
	 *
	 * @param job 打开job
	 */
	private void open(Job job) throws SalyutException{
		this.job = job;
		ready();
	}

	/**
	 * load
	 *
	 * 执行{@link Job#statements} 并 {@link STab#state} = {@link STab#BUSY}
	 */
	public ExecResult load() throws SalyutException{
		offerMessage("[engine]: script start running.");
		offerMessage("[proto]:{\"scriptState\":\"running\",\"missionId\":\""+job.id+"\"}");
		busy();
		ExecResult ret = executor.execute(job.statements);
		reset();
		return ret;
	}

	/**
	 * open
	 *
	 * 创建{@link Job} 调用内部
	 *
	 * @see #open(Job)
	 * @param script trico脚本文件
	 * @param channelId web socket 通道id 可空
	 * @param missionId 任务id 可空
	 */
	public void open(String script,String channelId,String missionId) throws SalyutException{
		open(new Job.Builder()
				.statements(Statements.of(SToken.getList(SYaml.toObj(script),this,0L,null)))
				.channelId(channelId)
				.id(missionId)
                .script(script)
				.build());
	}

	public void jobStash(){
		this.getBrowser().jobStash(job);
		reset();
	}

	public void offerMessage(String content){
		offerMessageWithTag(content, MessageTag.None);
	}

	public void offerMessageWithTag(String content,MessageTag tag){
		browser.getOutput().offerWithTag(job.channelId, job.id, content, tag);
	}


	public static void processSegmentFile(String content) throws SalyutException{
		List<Segment> segmentList = SToken.getSegmentList(content);
		for (Segment segment:segmentList){
			Salyut.putSegment(segment.getName(),segment.yamlString());
		}
	}


	public SBrowser getBrowser() {
		return browser;
	}

	public void setBrowser(SBrowser browser) {
		this.browser = browser;
	}

	public Path getPath() {
		return job.path;
	}

}
