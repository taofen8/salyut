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

public class LambdaExprTokenParser {
    private String chars;
    private int moveIndex = 0;
    private LambdaToken curToken;
    private int lastChar = ' ';

    public LambdaExprTokenParser(String chars) {
        this.chars = chars;
    }

    public LambdaToken getCurToken() {
        return curToken;
    }


    private boolean isPointerBegin(char c){
        return c == '-';
    }

    private boolean isPointerEnd(char c){
        return c == '>';
    }

    private boolean isSpace(char c){
        return c == ' ';
    }

    private boolean isAlpha(char c){
        return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
    }

    private int getChar() {
        if (moveIndex == chars.length()) return LambdaToken.Type.EOF.code;
        return chars.charAt(moveIndex++);
    }

    public boolean isParam(){
        return curToken.type.equals(LambdaToken.Type.PARAM);
    }

    public boolean isPointer(){
        return curToken.type.equals(LambdaToken.Type.POINTER);
    }

    public boolean isBlock(){
        return curToken.type.equals(LambdaToken.Type.BLOCK);
    }

    public boolean isEOF(){
        return curToken.type.equals(LambdaToken.Type.EOF);
    }

    public void getNextToken() {
        curToken = getTok();
    }

    private LambdaToken getTok() {
        while (isSpace((char) lastChar))
            lastChar = getChar();

        if (isAlpha((char)lastChar)){
            String identifierStr = "";
            do{
                identifierStr += ((char) lastChar);
            }while (!isPointerBegin((char) (lastChar = getChar())) && ((char)(lastChar)) != ' ');

            return new LambdaToken.Builder().type(LambdaToken.Type.PARAM).str(identifierStr).build();
        }

        if (isPointerBegin((char) lastChar)){
            String identifierStr = "";
            identifierStr += ((char) lastChar);
            if (isPointerEnd((char) (lastChar = getChar()))){
                identifierStr += ((char) lastChar);
                return new LambdaToken.Builder().type(LambdaToken.Type.POINTER).str(identifierStr).build();
            }
        }

        if (isPointerEnd(((char) lastChar))){
            String identifierStr = "";
            while ((lastChar = getChar()) != LambdaToken.Type.EOF.code){
                identifierStr += ((char) lastChar);
            }
            return new LambdaToken.Builder().type(LambdaToken.Type.BLOCK).str(identifierStr).build();
        }

        if (Token.Type.EOF.code == lastChar) {
            return new LambdaToken.Builder().type(LambdaToken.Type.EOF).build();
        }

        int thisChar = lastChar;
        lastChar = getChar();

        return new LambdaToken.Builder().type(LambdaToken.Type.IDENTIFIER).str((char) thisChar).build();
    }
}
