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
import com.trico.salyut.expr.ID;

/**
 * <b>elif</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/elif/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "elif")
public class Elif extends SToken{

	@Attribute(name = "cond",exprScan = true, unique = true)
	private Object cond = null;


	@Override
	public void action() throws SalyutException {
		super.action();
		//just cast all type value to boolean
		setResult(new ID(cond).castBool());

	}

}
