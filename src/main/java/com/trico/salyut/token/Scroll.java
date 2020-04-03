package com.trico.salyut.token;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.Check;
import com.trico.salyut.exception.SalyutException;
import org.openqa.selenium.JavascriptExecutor;

@TokenMark(name = "scroll")
public class Scroll extends SToken{
    @Attribute(name = "x",exprScan = true)
    private Object x = 0L;
    @Attribute(name = "y",exprScan = true)
    private Object y = 0L;

    private static final String SCROLL_FORMAT = "window.scrollTo(%d,%d)";

    @Override
    public void action() throws SalyutException {
        super.action();

        Check.requireNumberType(x,this);
        Check.requireNumberType(y,this);

        ((JavascriptExecutor) atTab.driver).executeScript(String.format(SCROLL_FORMAT,x,y));
    }
}
