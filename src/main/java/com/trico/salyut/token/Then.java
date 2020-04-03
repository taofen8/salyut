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
import com.trico.salyut.exception.SalyutExceptionType;

/**
 * <b>then</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/then/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "then")
public class Then extends SToken implements Block{


	@Override
	public void action() throws SalyutException {
		super.action();

		if (!(prev instanceof If) && !(prev instanceof Elif)){
			throw new SalyutException(SalyutExceptionType.RuntimeError,this,"previous token must be `if` or `elif`");
		}

		if (prev instanceof Elif){
			SToken it  = prev;
			while(it != null && !(it instanceof If) && it.getLevel().equals(getLevel())){
				it = it.prev;
			}

			if (it == null || !it.getLevel().equals(getLevel())){
				throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not find the beginning `if`");
			}
			if (it != prev && it.getResult().equals(Boolean.TRUE)){
				return;
			}
		}

		if (prev.getResult().equals(Boolean.TRUE)){
			setExecResult(atTab.executor.execute(statements));
		}
	}



    @Override
    public String key() {
        return null;
    }
}
