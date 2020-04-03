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

package com.trico.salyut.browser;

import com.trico.salyut.Salyut;
import com.trico.salyut.log.Log;
import com.trico.salyut.log.Message;
import com.trico.salyut.log.MessageTag;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * 脚本执行日志处理器
 *
 * @author Shen Yin
 */
public class OutputRunner extends Thread{
	private ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();
	private Salyut.OutputListener outputListener;
	private final Object waiter = new Object();

	/**
	 * {@link com.trico.salyut.token.SToken}处理时输出的日志格式
	 * <p> [engine]: `time` `token name` `start/complete` execution.(at line: `line no`)
	 */
	public static final String OFFER_MESSAGE_FORMAT = "[engine]: %s %s %s execution.(at line: %d)";

	public OutputRunner(Salyut.OutputListener outputListener){
		this.outputListener = outputListener;
	}

	@Override
	public void run() {
		for (;;) {
			if (isInterrupted()) {
				return;
			}

			Message msg = messageQueue.poll();
			if (null == msg){
				try {
					synchronized (waiter) {
						waiter.wait();
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
					return;
				}
			}
			else{
				if (outputListener != null){
					outputListener.present(msg);
				}
			}
		}
	}
	
	public void offerWithTag(String channelId,String missionId,String content, MessageTag tag){
		Message msg = new Message();
		msg.setChannelId(channelId);
		msg.setMissionId(missionId);
		msg.setContent(content);
		msg.setTag(tag.getVaule());
		
		messageQueue.offer(msg);
		
		synchronized(waiter){
			waiter.notifyAll();
		}
	}
	
	public void offer(String channelId,String missionId,String content){
		offerWithTag(channelId,missionId,content,MessageTag.None);
	}
	
}
