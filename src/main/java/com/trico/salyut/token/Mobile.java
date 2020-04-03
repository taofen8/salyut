package com.trico.salyut.token;

import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

@TokenMark(name = "mobile")
public class Mobile extends SToken{
    @Override
    public void action() throws SalyutException {
        super.action();
        if (!atTab.getBrowser().isMobileMode()){
            atTab.jobStash();
            throw new SalyutException(SalyutExceptionType.MobileMode,this,"change to mobile mode.");
        }
    }
}