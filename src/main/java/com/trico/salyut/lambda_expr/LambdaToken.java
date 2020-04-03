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

package com.trico.salyut.lambda_expr;

import com.trico.salyut.expr.Token;

public class LambdaToken {
    public enum Type{
        EOF(-1,"eof"),
        PARAM(0,"param"),
        POINTER(1,"pointer"),
        BLOCK(2,"block"),
        IDENTIFIER(3,"identifier");

        public int code;
        String explain;
        Type(int code, String explain){
            this.code = code;
            this.explain = explain;
        }

        public boolean equals(Token.Type other){
            return code == other.code;
        }
    }

    public Type type;
    public String str;

    LambdaToken(Builder builder){
        this.type = builder.type;
        this.str = builder.str;
    }

    public static class Builder{
        private Type type;
        private String str;

        public Builder type(Type type){
            this.type = type;
            return this;
        }

        public Builder str(String str){
            this.str = new String(str);
            return this;
        }

        public Builder str(char c){
            this.str = new String(new char[]{c});
            return this;
        }


        public LambdaToken build(){
            return new LambdaToken(this);
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Token("+type.explain+") value:");
        builder.append(str);
        return builder.toString();
    }
}
