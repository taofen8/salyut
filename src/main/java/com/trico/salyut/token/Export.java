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

import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;

@TokenMark(name = "export")
public class Export extends SToken{

	private String path;
	private String value;

	@Override
	public void action() throws SalyutException {
		super.action();

	}

	
	public String getValue() {
		return this.value;
	}
	public String getPath() {
		return this.path;
	}

	
}