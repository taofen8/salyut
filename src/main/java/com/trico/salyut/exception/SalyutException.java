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

package com.trico.salyut.exception;

import com.trico.salyut.token.SToken;

public class SalyutException extends Exception{
	private static final long serialVersionUID = 6898761896406160161L;
	private SToken token;
	private SalyutExceptionType type;
	private String addonMessage;

	public SalyutException(SalyutExceptionType type,String addonMessage){
		super(type.getExplain()+addonMessage);
		this.type = type;
		this.addonMessage = addonMessage;
	}
	
	public SalyutException( SalyutExceptionType type,SToken token,String addonMessage){
		super(type.getExplain() + " line:"+token.getLine() + " token:" + token.getTokName()+ " "+(addonMessage == null ? "":addonMessage));
		this.token = token;
		this.type = type;
		this.addonMessage = addonMessage;
	}

	public SToken getToken() {
		return token;
	}

	public void setToken(SToken token) {
		this.token = token;
	}

	public SalyutExceptionType getType() {
		return type;
	}

	public void setType(SalyutExceptionType type) {
		this.type = type;
	}

	public String getAddonMessage() {
		return addonMessage;
	}

	public void setAddonMessage(String addonMessage) {
		this.addonMessage = addonMessage;
	}
}
