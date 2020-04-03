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

package com.trico.salyut.expr;

import com.trico.salyut.expr.ast.ExprASTException;

import java.util.List;

/**
 * ID
 * 表达式运算通用类型
 *
 * 支持算术运算 + - x / %
 * 支持条件运算 > >= < <= == != && ||
 *
 * @author shenyin
 * */

public class ID {
    private Long longVal;
    private Double doubleVal;
    private String strVal;
    private Boolean boolVal;
    private Object objVal;
    private Type type;

    public enum Type{
        Long(0),
        Double(1),
        Str(2),
        Bool(3),
        Object(4),
        Null(5);
        int code;
        Type(int code){
            this.code = code;
        }
    }

    public Object val(){
        if (type.equals(Type.Long)){
            return longVal;
        }
        else if (type.equals(Type.Double)){
            return doubleVal;
        }
        else if (type.equals(Type.Str)){
            return strVal;
        }
        else if (type.equals(Type.Bool)){
            return boolVal;
        }
        else {
            return objVal;
        }
    }

    public Boolean isNull(){
        return type.equals(Type.Null);
    }

    public ID(Object val){
        if (val == null){
            this.type = Type.Null;
        }
        else if (val instanceof Long){
            this.longVal = (Long)val;
            this.type = Type.Long;
        }
        else if (val instanceof Integer){
            this.longVal = ((Integer) val).longValue();
            this.type = Type.Long;
        }
        else if (val instanceof Double){
            this.doubleVal = (Double)val;
            this.type = Type.Double;
        }
        else if (val instanceof Float){
            this.doubleVal = ((Float) val).doubleValue();
            this.type = Type.Double;
        }
        else if (val instanceof String){
            this.strVal = (String)val;
            this.type = Type.Str;
        }
        else if (val instanceof Boolean){
            this.boolVal = (Boolean)val;
            this.type = Type.Bool;
        }
        else{
            this.objVal = val;
            this.type = Type.Object;
        }
    }

    public Long castLong() throws ExprASTException{
        if (longVal != null){
            return longVal;
        }

        if (doubleVal != null){
            return Long.parseLong(String.valueOf(Math.floor(doubleVal)));
        }

        if (strVal != null){
            try{
                return Long.parseLong(strVal);
            }catch (NumberFormatException e){
                throw new IDFormatException("Can not cast string: \""+strVal+"\" to Long type");
            }
        }

        if (objVal != null){
            throw new IDFormatException("Can not cast object: \""+objVal+"\" to Long type");
        }

        throw new IDNullValeException();
    }

    public Double castDouble() throws ExprASTException{
        if (doubleVal != null){
            return doubleVal;
        }

        if (longVal != null){
            return Double.parseDouble(String.valueOf(longVal));
        }

        if (strVal != null){
            try{
                return Double.parseDouble(strVal);
            }catch (NumberFormatException e){
                throw new IDFormatException("For input string: \""+strVal+"\"");
            }
        }

        if (objVal != null){
            throw new IDFormatException("Can not cast object: \""+objVal+"\" to Double type");
        }

        throw new IDNullValeException();
    }

    public String castStr() throws  ExprASTException{
        if (strVal != null){
            return strVal;
        }

        if (longVal != null){
            return String.valueOf(longVal);
        }

        if (doubleVal != null){
            return String.valueOf(doubleVal);
        }

        if (objVal != null){
            throw new IDFormatException("Can not cast object: \""+objVal+"\" to String type");
        }

        throw new IDNullValeException();
    }

    public Boolean castBool() throws  ExprASTException{
        if (boolVal != null){
            return boolVal;
        }

        if (longVal != null) {
            return longVal > 0;
        }

        if (doubleVal != null){
            return Long.parseLong(String.valueOf(Math.floor(doubleVal))) > 0;
        }

        if (strVal != null){
            return Boolean.TRUE;
        }

        if (objVal != null){
            throw new IDFormatException("Can not cast object: \""+objVal+"\" to Bool type");
        }

        throw new IDNullValeException();
    }

    public ID add(ID other) throws ExprASTException {
        if (type.equals(Type.Str)){
            return new ID(strVal + other.castStr());
        }
        else if (other.type.equals(Type.Str)){
            return new ID(castStr() + other.strVal);
        }
        else if (type.equals(Type.Long)){
            return new ID(longVal+other.castLong());
        }
        else if (type.equals(Type.Double)){
            return new ID(doubleVal+other.castDouble());
        }
        else if (type.equals(Type.Bool)){
            throw new IDFormatException(applyError("+","Boolean",other.val().getClass().getSimpleName()));
        }
        else if (type.equals(Type.Object) && other.type.equals(Type.Object)){
            if (objVal instanceof List && other.objVal instanceof List){
                for(Object o: ((List) other.objVal)){
                    ((List) objVal).add(o);
                }
                return new ID(objVal);
            }
            else{
                throw new IDFormatException(applyError("+",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
            }
        }
        else{
            throw new IDFormatException(applyError("+",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
        }
    }

    public ID minus(ID other) throws ExprASTException{
        if (type.equals(Type.Long)){
            return new ID(longVal - other.castLong());
        }
        else if (type.equals(Type.Double)){
            return new ID(doubleVal - other.castDouble());
        }
        else if (type.equals(Type.Str)){
            throw new IDFormatException(applyError("-","String",other.val().getClass().getSimpleName()));
        }
        else if (type.equals(Type.Bool)){
            throw new IDFormatException(applyError("-","Boolean",other.val().getClass().getSimpleName()));
        }
        else{
            throw new IDFormatException(applyError("-",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
        }
    }

    public ID multiply(ID other) throws ExprASTException{
        if (type.equals(Type.Long)){
            return new ID(longVal * other.castLong());
        }
        else if (type.equals(Type.Double)){
            return new ID(doubleVal * other.castDouble());
        }
        else if (type.equals(Type.Str)){
            throw new IDFormatException(applyError("*","String",other.val().getClass().getSimpleName()));
        }
        else if (type.equals(Type.Bool)){
            throw new IDFormatException(applyError("*","Boolean",other.val().getClass().getSimpleName()));
        }
        else{
            throw new IDFormatException(applyError("*",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
        }
    }

    public ID divide(ID other) throws ExprASTException{
        if (type.equals(Type.Long)){
            return new ID(longVal / other.castLong());
        }
        else if (type.equals(Type.Double)){
            return new ID(doubleVal / other.castDouble());
        }
        else if (type.equals(Type.Str)){
            throw new IDFormatException(applyError("/","String",other.val().getClass().getSimpleName()));
        }
        else if (type.equals(Type.Bool)){
            throw new IDFormatException(applyError("/","Boolean",other.val().getClass().getSimpleName()));
        }
        else{
            throw new IDFormatException(applyError("/",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
        }
    }

    public ID mod(ID other) throws ExprASTException{
        if (type.equals(Type.Long)){
            return new ID(longVal % other.castLong());
        }
        else if (type.equals(Type.Double)){
            return new ID(doubleVal % other.castDouble());
        }
        else if (type.equals(Type.Str)){
            throw new IDFormatException(applyError("%","String",other.val().getClass().getSimpleName()));
        }
        else if (type.equals(Type.Bool)){
            throw new IDFormatException(applyError("%","Boolean",other.val().getClass().getSimpleName()));
        }
        else{
            throw new IDFormatException(applyError("%",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
        }
    }

    public ID equals(ID other){
        if (isNull()){
            return new ID(other.isNull());
        }
        else{
            return new ID(val().equals(other.val()));
        }
    }

    public ID notEquals(ID other){
        return new ID(!equals(other).boolVal);
    }

    public ID gt(ID other) throws ExprASTException{
        if (type.equals(Type.Long)){
            if (other.type.equals(Type.Long)){
                return new ID(longVal > (Long)other.val());
            }
            else if (other.type.equals(Type.Double)){
                return new ID(doubleVal > (Double)other.val());
            }
        }
        else if (type.equals(Type.Double)){
            if (other.type.equals(Type.Long)){
                return new ID(doubleVal > (Long)other.val());
            }
            else if (other.type.equals(Type.Double)){
                return new ID(doubleVal > (Double)other.val());
            }
        }

        throw new IDFormatException(applyError(">",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
    }

    public ID lt(ID other) throws ExprASTException{
        if (type.equals(Type.Long)){
            if (other.type.equals(Type.Long)){
                return new ID(longVal < (Long)other.val());
            }
            else if (other.type.equals(Type.Double)){
                return new ID(doubleVal < (Double)other.val());
            }
        }
        else if (type.equals(Type.Double)){
            if (other.type.equals(Type.Long)){
                return new ID(doubleVal < (Long)other.val());
            }
            else if (other.type.equals(Type.Double)){
                return new ID(doubleVal < (Double)other.val());
            }
        }

        throw new IDFormatException(applyError("<",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
    }

    public ID gtEquals(ID other) throws ExprASTException{
        return new ID(!lt(other).boolVal);
    }

    public ID ltEquals(ID other) throws ExprASTException{
        return new ID(!gt(other).boolVal);
    }

    public ID and(ID other) throws ExprASTException{
        if (type.equals(Type.Bool) && other.type.equals(Type.Bool)){
            return new ID(boolVal && (Boolean) val());
        }

        throw new IDFormatException(applyError("&&",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
    }

    public ID or(ID other) throws ExprASTException{
        if (type.equals(Type.Bool) && other.type.equals(Type.Bool)){
            return new ID(boolVal || (Boolean) val());
        }

        throw new IDFormatException(applyError("||",val().getClass().getSimpleName(),other.val().getClass().getSimpleName()));
    }

    private static boolean isArithmeticOp(String op){
        return "+".equals(op) || "-".equals(op) || "*".equals(op) || "/".equals(op) || "%".equals(op);
    }


    public static ID calc(String op,ID left,ID right) throws ExprASTException{
        if (isArithmeticOp(op) && left != null && left.val() == null){
            throw new ExprASTException("Can not operate "+op+" when left value is null");
        }

        if (isArithmeticOp(op) && right != null && right.val() == null){
            throw new ExprASTException("Can not operate "+op+" when right value is null");
        }

        if (op.equals("!")){
            if (left != null){
                throw new ExprASTException("Can not operate ! with left value"+left.val());
            }

            return new ID(!right.castBool());
        }
        else if (op.equals("+")){
            if (left == null) {//单目结合 +1
                left = new ID(0L);
            }
            return left.add(right);
        }
        else if (op.equals("-")){
            if (left == null){ //单目结合 -1
                left = new ID(0L);
            }
            return left.minus(right);
        }
        else if (op.equals("*")){
            return left.multiply(right);
        }
        else if (op.equals("/")){
            return left.divide(right);
        }
        else if (op.equals("%")){
            return left.mod(right);
        }
        else if (op.equals("==")){
            return left.equals(right);
        }
        else if (op.equals("!=")){
            return left.notEquals(right);
        }
        else if (op.equals(">")){
            return left.gt(right);
        }
        else if (op.equals("<")){
            return left.lt(right);
        }
        else if (op.equals(">=")){
            return left.gtEquals(right);
        }
        else if (op.equals("<=")){
            return left.ltEquals(right);
        }
        else if (op.equals("&&")){
            return new ID(left.castBool() && right.castBool());
        }
        else if (op.equals("||")){
            return new ID(left.castBool() || right.castBool());
        }

        throw new ExprASTException("unknown operator: "+op);
    }

    private String applyError(String operator,String typeLeft,String typeRight){
        return "Can not apply operator \""+operator+"\" to "+typeLeft+" and "+typeRight;
    }

    public static class IDNullValeException extends ExprASTException{
        IDNullValeException(String message){
            super(message);
        }

        public IDNullValeException() {
            super("Can not get val inside ID");
        }
    }

    public static class IDFormatException extends  ExprASTException{
        IDFormatException(String message){
            super(message);
        }
    }
}
