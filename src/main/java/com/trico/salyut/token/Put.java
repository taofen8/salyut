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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

/**
 * <b>put</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/put/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "put")
public class Put extends SToken{
	@Attribute(name = "path",required = true)
	private String path = null;
	@Attribute(name = "value",exprScan = true)
	private Object value = null;
	@Attribute(name = "values")
	private String values = null;
	@Attribute(name = "regex" ,exprScan = true)
	private String regex = null;
	@Attribute(name = "icase", exprScan = true)
	private boolean icase = false;
	@Attribute(name = "lambda")
	private String lambda = null;

	private  static Pattern MAP_STYLE_PATTERN =  Pattern.compile("\" *:");


	@Override
	public void action() throws SalyutException {
		super.action();

		if (null != values) {
			//输入可能为List或者Map
			String[] putValueList = values.split(",");
			boolean isList = true;
			List<Object> list = new ArrayList<>();
			Map<String,Object> map = new HashMap<>();
			for (int i = 0; i < putValueList.length; i++){
				String putValue = putValueList[i];
				if (0 == i){
					isList = !MAP_STYLE_PATTERN.matcher(putValue).find();
				}
				else{
					//保证输入的类型一致
					if (isList ^ !MAP_STYLE_PATTERN.matcher(putValue).find()){
						throw  new SalyutException(SalyutExceptionType.RuntimeError,this,"input values have a key value item and a list item.");
					}
				}

				if (isList){
					list.add(getExprValue(putValue));
				}
				else{
					String[] keyValue = putValue.trim().split("\" *:");
					map.put(keyValue[0].substring(1),getExprValue(keyValue[1]));
				}
			}
			if (isList){
				setExprValue(path,list);
			}
			else{
				setExprValue(path,map);
			}

		} else {
			if (value instanceof String){
				resetValueWithRegexIfNeeded();
			}

			if (lambda != null){
				value = execLambda(lambda,value);
			}
			setExprValue(path, value);
		}
	}

	private void resetValueWithRegexIfNeeded(){
		if (null != regex){
			Pattern p;
			if (icase){
				p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
			}
			else{
				p = Pattern.compile(regex);
			}
			Matcher m = p.matcher(((String) value));
			if(m.find()){
				value = m.group(1);
			}
			else{
				value = null;
			}
		}
	}
}
