package com.trico.salyut.token;

import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.token.Block;
import com.trico.salyut.token.SToken;

@TokenMark(name = "try")
public class Try extends SToken implements Block {

    @Override
    public void action() throws SalyutException {
        super.action();
        ExecResult result;
        try{
            result = atTab.executor.execute(statements);
        }
        catch (SalyutException e){
            result = atTab.executor.execute(catchStatements);
        }

        setExecResult(result);
    }

    @Override
    public String key(){
        return "do";
    }

    @Override
    public boolean needCatch() {
        return true;
    }
}
