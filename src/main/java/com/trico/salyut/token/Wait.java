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
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * <b>wait</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/wait/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "wait")
public class Wait extends SToken{
	@Attribute(name = "type", exprScan = true)
	private String type = null;
	@Attribute(name = "ele" ,exprScan = true,rebuildEle = true)
	private Object ele = null;
	@Attribute(name = "timeout", exprScan = true)
	private Long timeout = 60000L;
	@Attribute(name = "millis" ,exprScan = true)
	private Long millis = null;

    @Override
    public void action() throws SalyutException {
        super.action();
        try{
            this.setResult(1);
            if ("presence".equals(type)){
                (new WebDriverWait(atTab.driver, timeout/1000))
                        .until(ExpectedConditions.presenceOfElementLocated(By.cssSelector((String) ele)));
            }
            else if ("visibility".equals(type)){
                if (ele instanceof WebElement){
                    (new WebDriverWait(atTab.driver, timeout/1000))
                            .until(ExpectedConditions.visibilityOf((WebElement) ele));
                }
                else{
                    (new WebDriverWait(atTab.driver, timeout/1000))
                            .until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector((String)ele)));
                }

            }
            else if ("clickable".equals(type)){
                if (ele instanceof WebElement){
                    (new WebDriverWait(atTab.driver, timeout/1000))
                            .until(ExpectedConditions.elementToBeClickable((WebElement) ele));
                }
                else{
                    (new WebDriverWait(atTab.driver, timeout/1000))
                            .until(ExpectedConditions.elementToBeClickable(By.cssSelector((String) ele)));
                }

            }
            else if ("time".equals(type)){
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }catch(TimeoutException e){
            this.setResult(0);
        }
    }
}
