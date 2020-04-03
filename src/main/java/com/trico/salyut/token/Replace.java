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

/**
 * <b>replace</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/replace/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "replace")
public class Replace extends SToken {
	@Attribute(name = "path", required = true)
	private String path = null;
	@Attribute(name = "regex", exprScan = true)
	private String regex = null;
	@Attribute(name = "replacement", exprScan = true)
	private String replacement = null;

	@Override
	public void action() throws SalyutException{
		super.action();
		Object o = getExprValue("$"+path);
		if (!(o instanceof String)){
			throw new SalyutException(SalyutExceptionType.RuntimeError,this,"this token just use for `String` type");
		}
		String r = ((String)o).replaceAll(regex, replacement);
		setExprValue(path,r);
		setResult(r);
	}
}
