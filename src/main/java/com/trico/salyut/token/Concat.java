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
import com.trico.salyut.exception.SalyutExceptionType;

@Deprecated
@TokenMark(name = "concat")
public class Concat extends SToken {
	@Attribute(name = "path", required = true)
	private String path = null;
	@Attribute(name = "value", exprScan = true)
	private Object value = null;

	@Override
	public void action() throws SalyutException{
		super.action();
		Object o = getExprValue("$"+path);
		if (o == null) o = "";
		if (!(o instanceof String) || !(value instanceof String)){
			throw new SalyutException(SalyutExceptionType.RuntimeError,this,"this token just use for `String` type");
		}

		setExprValue(path,o+(String)value);
	}

}
