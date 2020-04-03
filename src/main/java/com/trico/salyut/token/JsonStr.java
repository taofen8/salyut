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

import com.alibaba.fastjson.JSON;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

import java.util.Map;

/**
 * <b>jsonstr</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/jsonstr/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "jsonstr")
public class JsonStr extends SToken {
    @Attribute(name = "path",required = true)
    private String path = null;
    @Attribute(name = "value",exprScan = true)
    private Object value = null;

    @Override
    public void action() throws SalyutException {
        super.action();
        String json;
        try{
            json = JSON.toJSONString(value,true).replace("\\\\n","\n");
        }catch (Exception e){
            throw new SalyutException(SalyutExceptionType.RuntimeError,this,"to json string failed:"+e.getMessage());
        }
        setExprValue(path, json);
    }
}
