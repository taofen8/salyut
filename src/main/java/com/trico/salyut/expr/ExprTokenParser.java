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

import java.util.List;
import java.util.stream.Stream;

/**
 * ExprTokenParser
 * 表达式词法解析器
 *
 * 通过该类，表达式将会被标记为以下类型
 * {@link Token}
 *
 * EOF          表达式结尾
 * Identifier   一般标识符
 * Path         路径
 * NumInt       整型数值
 * NumDouble    浮点数数值
 * Operator     操作符
 * Str          字符串
 *
 * @author shenyin
 **/
public class ExprTokenParser {
    private String chars;
    private int moveIndex = 0;
    private int rollbackIndex = -1;
    private Token curToken;
    private int lastChar = ' ';

    public ExprTokenParser(String chars) {
        this.chars = chars;
    }

    public Token getCurToken() {
        return curToken;
    }


    public void getNextToken() {
        curToken = getTok();
    }

    public boolean isIdentifier() {
        return curToken.type.equals(Token.Type.IDENTIFIER);
    }

    public boolean isPath() {
        return curToken.type.equals(Token.Type.PATH);
    }

    public boolean isNumInt() {
        return curToken.type.equals(Token.Type.NUM_INT);
    }

    public boolean isNumDouble() {
        return curToken.type.equals(Token.Type.NUM_DOUBLE);
    }

    public boolean isOperator() {
        return curToken.type.equals(Token.Type.OPERATOR);
    }

    public boolean isStr(){
        return curToken.type.equals(Token.Type.STR);
    }

    public boolean isEOF() {
        return curToken.type.equals(Token.Type.EOF);
    }

    public boolean isCssSelector(){
        return curToken.type.equals(Token.Type.CSS_SELECTOR);
    }

    public boolean isUndefined(){
        return curToken.type.equals(Token.Type.UNDEFINED);
    }

    public boolean isReversed(){
        return curToken.type.equals(Token.Type.REVERSED);
    }



    private Token getTok() {
        while (isSpace((char) lastChar))
            lastChar = getChar();

        if (isSlash((char) lastChar)) {
            String identifierStr = "";
            lastChar = getChar();
            while (!isSlash((char) lastChar)
                    && !isOperatorBegin((char) lastChar)
                    && (char) lastChar != '['
                    && (char) lastChar != ']'
                    && (char) lastChar != ')'
                    && (char) lastChar != ' '
                    && (char) lastChar != '\\'
                    && lastChar != Token.Type.EOF.code) {
                identifierStr += (char) lastChar;
                lastChar = getChar();
            }
            return new Token.Builder().type(Token.Type.PATH).str(identifierStr).build();
        }

        if (isDigit((char) lastChar) && !preCharIsDollar()) {
            String numStr = "";
            do {
                numStr += (char) lastChar;
            } while (isDigit((char) (lastChar = getChar())));

            if (numStr.contains(".")) {
                return new Token.Builder().type(Token.Type.NUM_DOUBLE).num(Double.parseDouble(numStr)).build();
            } else {
                return new Token.Builder().type(Token.Type.NUM_INT).num(Long.parseLong(numStr)).build();
            }
        }

        if (isQuote((char) lastChar)) {
            String identifierStr = "";
            lastChar = getChar();
            while (!isQuote((char) lastChar) || (isQuote((char) lastChar) && preCharIsEscape())) {
                if (!isEscape((char) lastChar) || (isEscape((char) lastChar) && preCharIsEscape())){ //do not add escape \
                    identifierStr += (char) lastChar;
                }
                lastChar = getChar();
                if (Token.Type.EOF.code == lastChar) {
                    return new Token.Builder().type(Token.Type.UNDEFINED).str((char) lastChar).build();
                }
            }
            lastChar = getChar(); //eat "
            return new Token.Builder().type(Token.Type.STR).str(identifierStr).build();
        }

        if (isOperatorBegin((char) lastChar)) {
            String identifierStr = "";
            do {
                identifierStr += (char) lastChar;
            } while (isOperatorEnd((char) (lastChar = getChar())));

            return new Token.Builder().type(Token.Type.OPERATOR).str(identifierStr).build();
        }

        if (isEscape((char) lastChar)){  //process /
            String identifierStr = "";
            lastChar = getChar();
            identifierStr += (char) lastChar;
            lastChar = getChar();
            return new Token.Builder().type(Token.Type.OPERATOR).str(identifierStr).build();
        }

        if (isAt((char) lastChar)){
            String identifierStr = "";
            lastChar = getChar();
            while (lastChar != Token.Type.EOF.code){
                identifierStr += (char) lastChar;
                lastChar = getChar();
            }
            return new Token.Builder().type(Token.Type.CSS_SELECTOR).str(identifierStr).build();
        }

        if (isReversedBegin((char) lastChar) && preCharIsDollar()){
            if ((char)lastChar == 'e'
                    || (char)lastChar == '0'
                    || (char)lastChar == '1'
                    || (char)lastChar == 'v'
                    || (char)lastChar == 'i'){
                return oneCharReversedCap();
            }
            else{
                Token reversedToken = null;
                if ((char)lastChar == 't'){
                    reversedToken = multiCharReversedCap(new char[][]{trueRestChars()});
                }
                else if ((char)lastChar == 'f') {
                    reversedToken = multiCharReversedCap(new char[][]{falseRestChars()});
                }
                else if ((char)lastChar == 'c'){
                    reversedToken = multiCharReversedCap(new char[][]{countRestChars()});
                }
                else if ((char)lastChar == 'n'){
                    reversedToken = multiCharReversedCap(new char[][]{nowRestChars(),nullRestChars()});
                }

                if (reversedToken == null){ //eat the next char and return unknown
                    return new Token.Builder().type(Token.Type.UNDEFINED).str((char) lastChar).build();
                }
                else {
                    return reversedToken;
                }
            }
        }

        if (Token.Type.EOF.code == lastChar) {
            return new Token.Builder().type(Token.Type.EOF).build();
        }

        int thisChar = lastChar;
        lastChar = getChar();

        return new Token.Builder().type(Token.Type.IDENTIFIER).str((char) thisChar).build();
    }

    private boolean isSpace(char c) {
        return c == ' ';
    }

    private boolean isQuote(char c) {
        return c == '"';
    }

    private boolean isEscape(char c){
        return c == '\\';
    }

    private boolean isSlash(char c) {
        return c == '/';
    }


    private boolean isDigit(char c) {
        return c >= '0' && c <= '9' || c == '.';
    }

    private boolean isOperatorBegin(char c) {
        return c == '>' || c == '<' || c == '=' || c == '!'
                || c == '+' || c == '-' || c == '*' || c == '/' || c == '%'
                || c == '&' || c == '|';
    }

    private boolean isReversedBegin(char c){
        return c == 'e' || c == 't' || c == 'f' || c == '0' || c == '1' || c == 'v' || c == 'i' || c == 'c' || c == 'n';
    }

    private char[] trueRestChars(){
        return new char[]{'r','u','e'};
    }

    private char[] falseRestChars(){
        return new char[]{'a','l','s','e'};
    }


    private char[] countRestChars(){
        return new char[]{'o','u','n','t'};
    }

    private char[] nowRestChars(){
        return new char[]{'o','w'};
    }

    private char[] nullRestChars(){
        return new char[]{'u','l','l'};
    }


    private Token oneCharReversedCap(){
        String identifierStr = "";
        identifierStr += (char)lastChar;
        lastChar = getChar();
        return new Token.Builder().type(Token.Type.REVERSED).str(identifierStr).build();
    }


    private Token multiCharReversedCap(char[][] possibleRestCharsList){
        String firstWord = new String(new char[]{(char)lastChar});
        for (char[] restChars:possibleRestCharsList){
            String identifierStr = "";
            int idx = 0;
            boolean isRollback = false;
            while (idx < restChars.length){
                char cmp = restChars[idx];
                if ((char) (lastChar = getRollbackChar()) == cmp){
                    identifierStr += (char) lastChar;
                    idx++;
                }
                else{
                    rollback();
                    isRollback = true;
                    break;
                }
            }
            if (!isRollback){
                lastChar = getRollbackChar();
                rollbackCharCommit();
                return new Token.Builder().type(Token.Type.REVERSED).str(firstWord+identifierStr).build();
            }

        }

        rollback();
        lastChar = getChar();
        return null;
    }


    private boolean isOperatorEnd(char c) {
        return c == '=' || c == '&' || c == '|';
    }

    private boolean isAt(char c){
        return c == '@';
    }

    private int getChar() {
        if (moveIndex == chars.length()) return Token.Type.EOF.code;
        return chars.charAt(moveIndex++);
    }

    /**
     * 可回退的读取下个字符，主要用于多次预测的场景
     */
    private int getRollbackChar(){
        if (rollbackIndex == -1){
            rollbackIndex = moveIndex;
        }

        if (rollbackIndex == chars.length()) return Token.Type.EOF.code;
        return chars.charAt(rollbackIndex++);
    }

    /**
     * 当当前次预测失败，回退继续下次预测
     */
    private void rollback(){
        rollbackIndex = -1;
    }

    /**
     * 当预测成功，需要将可回退的index提交给moveIndex
     */
    private void rollbackCharCommit(){
        moveIndex = rollbackIndex;
        rollback();
    }

    private boolean preCharIsDollar(){
        if (moveIndex - 2 < 0) return false;
        else return chars.charAt(moveIndex -2) == '$';
    }


    private boolean preCharIsEscape(){
        if (moveIndex - 2 < 0) return false;
        else return chars.charAt(moveIndex -2) == '\\';
    }
}
