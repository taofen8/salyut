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
    @Attribute(name = "ele" ,exprScan = true,rebuildEle = true)
    private Object ele = null;

    @Override
    public void action() throws SalyutException {
        super.action();

        if (null == ele){
            throw new SalyutException(SalyutExceptionType.SeleniumError,this,"ele is null");
        }

        try{
            (new WebDriverWait(atTab.driver, 3))
                    .until(ExpectedConditions.elementToBeClickable((WebElement) ele));
            ((WebElement) ele).click();
            this.setResult(1);
        }catch (TimeoutException e){
            this.setResult(0);
        }
    }
}
