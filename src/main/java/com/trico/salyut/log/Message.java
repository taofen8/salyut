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

package com.trico.salyut.log;

public class Message {
	private String content;
	private String missionId;
	private String channelId;
	private String tag;
	
	public String getTag() {
		return tag;
	}
	public void setTag(String tag) {
		this.tag = tag;
	}
	public String getChannelId() {
		return channelId;
	}
	public void setChannelId(String channelId) {
		this.channelId = channelId;
	}
	public String getContent() {
		return content;
	}
	public String getMissionId() {
		return missionId;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public void setMissionId(String missionId) {
		this.missionId = missionId;
	}
}
