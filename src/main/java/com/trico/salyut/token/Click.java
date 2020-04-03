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
 * <b>click</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/click/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "click")
public class Click extends SToken{
	@Attribute(name = "ele", required = true,rebuildEle = true)
	private String ele = null;
	@Attribute(name = "under", rebuildEle = true)
	private String under = null;
	@Attribute(name = "js", exprScan = true)
	private boolean js = false;

	/**
	 * 点击页面元素
	 */
	@Override
	public void action() throws SalyutException {
		super.action();
		if (under != null){
			setSubFinder((WebElement) getExprValue(under));
		}

		WebElement webElement = (WebElement) getExprValue(ele);

		if (js){
			atTab.driver.executeScript("var tar=arguments[0];tar.click();", webElement);
		}
		else{
			webElement.click();
		}
	}

}
