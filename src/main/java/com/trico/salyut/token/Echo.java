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
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.log.MessageTag;

/**
 * <b>echo</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/echo/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "echo")
public class Echo extends SToken{
	@Attribute(name = "exp", exprScan = true, unique = true)
	private Object exp = null;

	
	@Override
	public void action() throws SalyutException{
		super.action();
		
		String echo;

		if (null == exp){
			atTab.offerMessageWithTag("[echo]:"+null, MessageTag.Echo);
			return;
		}

		if (exp instanceof String){
			echo = (String) exp;
		}
		else if (exp instanceof List){
			echo = JSON.toJSONString(exp, true);
		}
		else if (exp instanceof Map){
			echo = JSON.toJSONString(exp, true);
		}
		else{
			echo = exp.toString();
		}
		atTab.offerMessageWithTag("[echo]:"+echo, MessageTag.Echo);
	}
}
