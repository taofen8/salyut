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
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.exception.SalyutException;

import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.path.Path;

/**
 * <b>return</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/return/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "return")
public class Return extends SToken{
	@Attribute(name = "ret",unique = true)
	private String ret = null;

    /**
     * 将返回值 通过 {@link #setResult(Object)} 放入保留字
     * 并将返回值放入ExecResult中
     */
	@Override
	public void action() throws SalyutException{
		super.action();
		Object val = getExprValue(ret);
		setResult(val);
		setExecResult(ExecResult.getReturn(val));
	}

}
