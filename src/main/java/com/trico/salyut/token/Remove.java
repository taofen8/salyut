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

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;

/**
 * <b>remove</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/remove/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "remove")
public class Remove extends SToken{
	@Attribute(name = "path", required = true)
	private String path = null;

	
	@Override
	public void action() throws SalyutException{
		super.action();
		removeExprValue(path);
	}
}
