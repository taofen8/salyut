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

package com.trico.salyut.lambda_expr.ast;

import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

public class LambdaExprASTException extends SalyutException {
    public LambdaExprASTException(String message){
        super(SalyutExceptionType.LambdaExprASTError,message);
    }
}
