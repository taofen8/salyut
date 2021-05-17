package com.trico.salyut.token;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.*;
import java.util.Random;


//TODO:
//Temporary version
@TokenMark(name = "slide")
public class Slide extends SToken {
    @Attribute(name = "ele" ,exprScan = true,rebuildEle = true)
    private Object ele = null;
    @Attribute(name = "len",exprScan = true)
    private Object len = 0L;

    static Random random = new Random();
    @Override
    public void action() throws SalyutException {
        super.action();
        if (ele != null) {
            Actions act = new Actions(atTab.driver);
            WebElement webElement = (WebElement) ele;
            act.clickAndHold(webElement).build().perform();
            int length = ((Long) len).intValue();
            while (length > 0) {
                int offset = -30 + random.nextInt(120);

                if (length - offset < 0) {
                    offset = length;
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                act.moveByOffset(offset, 0).build().perform();
                length -= offset;
                int sleepTime = random.nextInt(2);
                try {
                    act.pause(sleepTime).build().perform();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            act.pause(3).build().perform();
            act.release().build().perform();
        }
    }


}
