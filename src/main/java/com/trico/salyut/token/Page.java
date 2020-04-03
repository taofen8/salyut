package com.trico.salyut.token;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

@TokenMark(name = "page")
public class Page extends SToken implements Block{
    @Attribute(name = "ele",underKey = "next", rebuildEle = true)
    private String ele;

    @Attribute(name = "limit",underKey = "next", exprScan = true)
    private long limit = -1L;

    private final static long EXCEPTION_LIMIT = 10000;


    @Override
    public void action() throws SalyutException {
        super.action();

        long i = 0;
        while (true){
            boolean breakCond = (i >= limit && limit > 0) || elementInvalid();
            if (breakCond){
                setExecResult(ExecResult.getThrough());
                return;
            }

            if (i > EXCEPTION_LIMIT){
                throw  new SalyutException(SalyutExceptionType.DeadLoop, this, "loop times is over "+EXCEPTION_LIMIT);
            }

            ExecResult result;
            try{
                result = atTab.executor.execute(statements);
            }
            catch (SalyutException e){
                throw new SalyutException(SalyutExceptionType.RuntimeError,e.getToken(),e.getAddonMessage()+ " at loop times "+i);
            }

            if (result.intType.equals(ExecResult.InterruptType.BREAK)){
                setExecResult(ExecResult.getThrough());
                return;
            }
            else if (result.intType.equals(ExecResult.InterruptType.RETURN)){
                setExecResult(result);
                return;
            }

            i++;
        }
    }

    private boolean elementInvalid() throws SalyutException{
        if (null == ele){
            return false;
        }

        WebElement target = null;

        try{
            target = (WebElement) getExprValue(ele);
        }catch (Exception e){
            if (e instanceof SalyutException){
                if (!e.getMessage().contains("Unable to locate element")){
                    throw e;
                }
            }
        }

        ExpectedCondition<WebElement> e = ExpectedConditions.elementToBeClickable(target);
        if (target != null &&  !"element to be clickable: null".equals(e.toString()) && attrProbe(target)){
            target.click();
            return false;
        }

        return true;
    }

    private boolean attrProbe(WebElement e){
        String txt = e.getAttribute("disabled");
        boolean txtNotEmpty = txt != null && txt.length() > 0;
        return !txtNotEmpty;
    }

    @Override
    public String key() {
        return "each";
    }
}
