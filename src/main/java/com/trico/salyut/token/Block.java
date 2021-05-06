package com.trico.salyut.token;

public interface Block {
    String key();
    default  boolean needCatch() {return false;}
}
