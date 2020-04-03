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

import com.alibaba.fastjson.JSON;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;

/**
 * <b>copy</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/copy/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "copy")
public class Copy extends SToken {
	@Attribute(name = "path", required = true)
	private String path = null;
	@Attribute(name = "value", exprScan = true)
	private Object value = null;
	

	@Override
	public void action() throws SalyutException{
		super.action();
		String jsonStr = JSON.toJSONString(value);
		setExprValue(path,"null".equals(jsonStr) ? null:JSON.parseObject(jsonStr, value.getClass()));
	
	}

	@SuppressWarnings("unused") //TokenBuilder 用于外部类创建copy token所以可以忽略unused
	public static class TokenBuilder{
		private String path;
		private String value;

		public static TokenBuilder newBuilder(){
			return new Copy.TokenBuilder();
		}


		public TokenBuilder path(String path){
			this.path = path;
			return this;
		}

		public TokenBuilder value(String value){
			this.value = value;
			return this;
		}

		@Override
		public String toString(){
			return String.format("- copy: { path: '%s', value: '%s' }",path,value);
		}
	}


}
