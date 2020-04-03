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
import org.openqa.selenium.WebElement;

/**
 * <b>find</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/find/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "find")
public class Find extends SToken{
	@Attribute(name = "ele", rebuildEle = true)
	private String ele = null;
	@Attribute(name = "under",  rebuildEle = true)
	private String under = null;
	@Attribute(name = "path")
	private String path = null;

	
	@Override
	public void action() throws SalyutException{
		super.action();

		WebElement target = null;

		try{
			if (under != null){
				setSubFinder(((WebElement) getExprValue(under)));
			}

			target = (WebElement) getExprValue(ele);
		}catch (Exception e){
			if (e instanceof SalyutException){
				/** 需要添加该catch 需要当 setSubFinder(((WebElement) getExprValue(under))) 或 (WebElement) getExprValue(ele)
				 * 出错时不要直接退出，而是通过setResult来设置result为false*/
				if (!e.getMessage().contains("Unable to locate element")){
					throw e;
				}
			}
		}

		if (path != null){
			setExprValue(path,target);
		}
		setResult(target == null ? 0:1);
	}

}
