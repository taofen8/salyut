package com.trico.salyut.exception;

import com.trico.salyut.token.SToken;

import java.util.List;

public final class Check {
    public static <T> T requireListType(T obj, SToken token) throws SalyutException{
        if (!(obj instanceof List)){
            throw new SalyutException(SalyutExceptionType.RuntimeError,token,"values must be a `List`");
        }

        return obj;
    }

    public static <T> T requireNumberType(T obj, SToken token) throws SalyutException{
        if (obj instanceof Long || obj instanceof Integer || obj instanceof Double || obj instanceof Float){
            return obj;
        }
        throw new SalyutException(SalyutExceptionType.RuntimeError,token,"values must be a `Number`");
    }

    public static <T> T requireNonNull(T obj,String msg) throws SalyutException{
        if (null == obj){
            throw new SalyutException(SalyutExceptionType.RuntimeError,msg);
        }

        return obj;
    }
}
