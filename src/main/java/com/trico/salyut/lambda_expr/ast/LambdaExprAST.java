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

import com.trico.salyut.expr.ID;
import com.trico.salyut.lambda_expr.LambdaExprASTContext;
import com.trico.salyut.lambda_expr.LambdaExprTokenParser;

public abstract class LambdaExprAST {
    protected LambdaExprASTContext context;
    protected LambdaExprTokenParser parser;
    protected ID val;

    public LambdaExprAST(LambdaExprASTContext context, LambdaExprTokenParser parser, Object ... otherParams) throws LambdaExprASTException{
        this.context = context;
        this.parser = parser;
        construct(otherParams);
    }

    public ID getVal() throws LambdaExprASTException {
        return val;
    }
    public abstract void construct(Object ... otherParams) throws LambdaExprASTException;
}
