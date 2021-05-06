package com.trico.salyut.token;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.Check;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.token.SToken;
import org.openqa.selenium.JavascriptExecutor;

@TokenMark(name = "walkthrough")
public class WalkThrough extends SToken {
    @Attribute(name = "step",exprScan = true)
    private Object step = 0L;

    private static final String SCROLL_Y_FORMAT = "window.scrollTo(0,%d)";
    @Override
    public void action() throws SalyutException {
        super.action();
        Check.requireNumberType(step,this);
        Long height = (Long)((JavascriptExecutor) atTab.driver).executeScript("return window.scrollMaxY");
        if (height != null){
            Long goThrough = 0L;
            while (goThrough < height){
                ((JavascriptExecutor) atTab.driver).executeScript(String.format(SCROLL_Y_FORMAT,goThrough));
                goThrough += (Long) step;
            }
        }
    }
}
