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

package com.trico.salyut.expr.ast;

import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

public class ExprASTException extends SalyutException {
    public ExprASTException(String message){
        super(SalyutExceptionType.ExprASTError, message);
    }
}
