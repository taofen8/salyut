package com.trico.salyut.token;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.token.SToken;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

@TokenMark(name = "safeclick")
public class SafeClick extends SToken {
    @Attribute(name = "ele" ,rebuildEle = true)
    private String ele = null;
    @Attribute(name = "under", rebuildEle = true)
    private String under = null;

    @Override
    public void action() throws SalyutException {
        super.action();


        if (under != null){
            setSubFinder((WebElement) getExprValue(under));
        }

        WebElement webElement = (WebElement) getExprValue(ele);

        if (null == webElement){
            throw new SalyutException(SalyutExceptionType.SeleniumError,this,"ele is null");
        }


        try{
            (new WebDriverWait(atTab.driver, 3))
                    .until(ExpectedConditions.elementToBeClickable(webElement));
            webElement.click();
            this.setResult(1);
        }catch (TimeoutException e){
            this.setResult(0);
        }
    }
}
