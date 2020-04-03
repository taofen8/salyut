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
import com.trico.salyut.exception.SalyutExceptionType;

import java.util.List;
import java.util.Map;

/**
 * <b>len</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/len/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "len")
public class Len extends SToken {
    @Attribute(name = "exp", exprScan = true, unique = true)
    private Object exp = null;

    @Override
    public void action() throws SalyutException{
        super.action();

        if (null == exp){
            throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not use this token to a null object.");
        }

        if (exp instanceof String){
            setResult(((String) exp).length());
        }
        else if (exp instanceof List){
            setResult(((List) exp).size());
        }
        else if (exp instanceof Map){
            setResult(((Map) exp).size());
        }
        else{
            setResult(0);
        }
    }
}
