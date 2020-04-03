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
import com.trico.salyut.expr.ID;
import com.trico.salyut.exception.SalyutException;
import org.openqa.selenium.WebElement;

import com.trico.salyut.STab;

/**
 * <b>fill</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/fill/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "fill")
public class Fill extends SToken{
	@Attribute(name = "ele", exprScan = true, rebuildEle = true)
	private Object ele = null;
	@Attribute(name = "value", exprScan = true)
	private Object value = null;

	@Override
	public void action() throws SalyutException {
		super.action();

		WebElement webElement =  ((WebElement) ele);
		if (webElement.isDisplayed()){
			webElement.clear();
			webElement.sendKeys(new ID(value).castStr());
		}
		
	}
}
