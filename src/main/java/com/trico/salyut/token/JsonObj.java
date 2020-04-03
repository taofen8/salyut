package com.trico.salyut.token;

import com.alibaba.fastjson.JSON;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

import java.util.Map;

/**
 * <b>jsonobj</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/jsonobj/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "jsonobj")
public class JsonObj extends SToken {
    @Attribute(name = "path",required = true)
    private String path = null;
    @Attribute(name = "value",exprScan = true)
    private String value = null;

    @Override
    public void action() throws SalyutException {
        super.action();
        Object json;
        try{
            json = JSON.parseObject(value);
        }catch (Exception e){
            throw new SalyutException(SalyutExceptionType.RuntimeError,this,"to json object failed:"+e.getMessage());
        }
        setExprValue(path, json);
    }
}
