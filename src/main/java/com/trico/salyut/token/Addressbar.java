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

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;


/**
 * <b>addressbar</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/addressbar/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "addressbar")
public class Addressbar extends SToken{
	@Attribute(name = "path",required = true)
	private String path = null;
	@Attribute(name = "regex")
	private String regex = null;

	@Override
	public void action() throws SalyutException{
		super.action();
		
		if (path != null){
		    setExprValue(path,atTab.driver.getCurrentUrl());
		}
		
		if (regex != null){
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(atTab.driver.getCurrentUrl());
			if (m.find()){
				setExprValue(path, m.group(1));
			}
			setResult(m.find() ? 1:0);
		}
	}

}
