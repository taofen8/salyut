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
 * <b>load</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/load/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "load")
public class Load extends SToken{
	@Attribute(name = "url", exprScan = true,unique = true)
	private String url = null;

	@Override
	public void action() throws SalyutException {
		super.action();
		atTab.driver.get(url);
	}
	
}
