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

import java.util.List;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

/**
 * <b>else</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/else/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "else")
public class Else extends SToken implements Block{
	private List<SToken> tokenList;
	private static final int FOCUS_BREAK_COUNT = 1000;

	@Override
	public void action() throws SalyutException {
		super.action();
		SToken it  = this.prev;
		for(int i = 0; i < FOCUS_BREAK_COUNT;i++){
			if (null == it || !it.getLevel().equals(getLevel())){
				throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not find the beginning `if`");
			}

			if (it.isElif()){
				if (it.getResult().equals(Boolean.TRUE)){
					return;
				}
			}
			else if (it.isIf()){
				if (it.getResult().equals(Boolean.TRUE)){
					return;
				}
				else{
					break;
				}
			}

			it = it.prev;
		}
		setExecResult(atTab.executor.execute(statements));
	}

	@Override
	public String key() {
		return null;
	}
}
