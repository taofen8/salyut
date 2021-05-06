package com.trico.salyut.engine;

import com.trico.salyut.browser.OutputRunner;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.token.SToken;
import com.trico.salyut.token.Segment;
import com.trico.salyut.token.Then;
import com.trico.salyut.utils.TimeUtil;

import java.util.Date;

public class Executor<T extends SToken> {
    public ExecResult execute(Statements<T> statements) throws SalyutException {
        statements.getNextTok();
        ExecResult result = ExecResult.getThrough();
        while (!statements.isEOF()){
            T tok = statements.curTok();
            /** segment 交给 {@link com.trico.salyut.token.Callin}处理 */
            if (tok instanceof Segment){
                statements.getNextTok();
                continue;
            }

            String tokName = tok.getTokName();
            if (tok.isIf() || tok.isElif()){
                tokName += " then";
            }

            if (!tok.avoidOutputMessage()){
                tok.getTab().offerMessage(String.format(OutputRunner.OFFER_MESSAGE_FORMAT, TimeUtil.getTime(new Date()),tokName,"start",tok.getLine()));
            }
            tok.action();

            if (!tok.avoidOutputMessage()){
                tok.getTab().offerMessage(String.format(OutputRunner.OFFER_MESSAGE_FORMAT, TimeUtil.getTime(new Date()),tokName,"complete",tok.getLine()));
            }

            result =  tok.restoreAndCopyResult();

            if (result.intType.equals(ExecResult.InterruptType.BREAK)
            || result.intType.equals(ExecResult.InterruptType.RETURN)
            ||result.intType.equals(ExecResult.InterruptType.CONTINUE)){
                statements.rollback();
                return result;
            }
            statements.getNextTok();
        }

        statements.rollback();
        return result;
    }
}

