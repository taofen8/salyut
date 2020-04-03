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

@TokenMark(name = "iframe")
public class Iframe extends SToken{
	@Attribute(name = "ele",exprScan = true)
	private String ele;

	@Override
	public void action() throws SalyutException {
		super.action();
		if(ele!=null){
			WebElement webElement  = atTab.driver.findElementByCssSelector(ele);
			atTab.driver.switchTo().frame(webElement);
		}else{
			atTab.driver.switchTo().defaultContent();
		}
	}
}
