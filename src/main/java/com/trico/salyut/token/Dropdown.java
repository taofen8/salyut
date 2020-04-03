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
 * <b>dropdown</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/dropdown/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "dropdown")
public class Dropdown extends SToken{
	@Attribute(name = "ele", exprScan = true, rebuildEle = true)
	private Object ele = null;
	@Attribute(name = "value", exprScan = true)
	private String value = null;


	@Override
	public void action() throws SalyutException {
		super.action();
		WebElement webElement;
		if (ele instanceof WebElement){
			webElement = ((WebElement) ele);
		}
		else{
			webElement = atTab.driver.findElementByCssSelector(((String) ele));
		}
		org.openqa.selenium.support.ui.Select select = new org.openqa.selenium.support.ui.Select(webElement);
		select.selectByVisibleText(value);
	}

}
