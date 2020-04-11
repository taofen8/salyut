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

import java.util.stream.Stream;

public  class Token{

    public enum Type{
        EOF(-1,"eof"),
        IDENTIFIER(0,"identifier"),
        NUM_INT(1,"num_int"),
        NUM_DOUBLE(2,"num_double"),
        PATH(3,"path"),
        STR(4,"str"),
        OPERATOR(5,"operator"),
        CSS_SELECTOR(6,"css_selector"),
        UNDEFINED(7,"undefined"),
        REVERSED(8,"reversed");

        public int code;
        String explain;
        Type(int code, String explain){
            this.code = code;
            this.explain = explain;
        }

        public boolean equals(Type other){
            return code == other.code;
        }
    }

    public Type type;
    public String str;
    public Double numDouble;
    public Long numInt;

    Token(Builder builder){
        this.type = builder.type;
        this.str = builder.str;
        this.numInt = builder.numInt;
        this.numDouble = builder.numDouble;
    }

    public static class Builder{
        private Type type;
        private String str;
        private Long numInt;
        private Double numDouble;

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

        public Builder num(Long numInt){
            this.numInt = numInt;
            return this;
        }

        public Builder num(Double numDouble){
            this.numDouble= numDouble;
            return this;
        }

        public Token build(){
            return new Token(this);
        }
    }

    public int getPrec(){
        if (type.equals(Type.OPERATOR)){
            return getOperatorPriority(str);
        }
        else{
            return -1;
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Token("+type.explain+") value:");
        if (type.equals(Type.NUM_INT)){
            builder.append(numInt);
        }
        else if (type.equals(Type.NUM_DOUBLE)){
            builder.append(numDouble);
        }
        else{
            builder.append(str);
        }

        return builder.toString();
    }

    private int getOperatorPriority(String s){
        if (s.equals("*") || s.equals("/") || s.equals("%"))
            return 140;
        else if (s.equals("+") || s.equals("-"))
            return 120;
        else if (s.equals("!"))
            return 110;
        else if (s.equals(">") || s.equals("<") || s.equals(">=") || s.equals("<="))
            return 100;
        else if (s.equals("==") || s.equals("!="))
            return 90;
        else if (s.equals("&"))
            return 80;
        else if (s.equals("^"))
            return 70;
        else if (s.equals("|"))
            return 60;
        else if (s.equals("&&"))
            return 50;
        else if (s.equals("||"))
            return 40;
        else if (s.equals("="))
            return 30;

        return -1;

    }
}