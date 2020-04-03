package com.trico.salyut.engine;

import com.trico.salyut.token.SToken;

import java.util.List;
import java.util.Objects;

public class Statements<T extends SToken> {
    private List<T> tokenList;
    private int moveIndex;
    private T curTok;
    private boolean eof;

    public static <T extends SToken> Statements<T> of(List<T> tokenList){
        Objects.requireNonNull(tokenList);
        return new Statements<>(tokenList);
    }

    private Statements(List<T> tokenList){
        this.tokenList = tokenList;
    }

    public T curTok(){
        return curTok;
    }

    public T getNextTok(){
        return this.curTok = getTok();
    }

    private T getTok(){
        if (moveIndex == tokenList.size()){
            this.eof = true;
            return null;
        }
        return tokenList.get(moveIndex++);
    }

    public boolean isEOF(){
        return eof;
    }

    public void rollback(){
        moveIndex = 0;
        eof = false;
    }
}
