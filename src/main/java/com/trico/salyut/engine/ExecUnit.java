package com.trico.salyut.engine;

import com.trico.salyut.exception.SalyutException;

public interface ExecUnit {
    boolean isReady();
    boolean isIdle();
    boolean isBusy();
    String jobId();
    ExecResult execute() throws SalyutException;
    void reset();
}
