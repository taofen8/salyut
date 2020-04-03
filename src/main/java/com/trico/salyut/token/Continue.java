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
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.exception.SalyutException;

/**
 * <b>continue</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/continue/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "continue")
public class Continue extends SToken{
	
	@Override
	public void action() throws SalyutException{
		super.action();
		setExecResult(ExecResult.getContinue());
	}
}
