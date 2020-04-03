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

import com.trico.salyut.expr.ID;
import com.trico.salyut.lambda_expr.ast.LambdaExprASTException;
import com.trico.salyut.lambda_expr.ast.LambdaFuncExprAST;

public class LambdaExprMainLoop {
    public static ID execLambda(String expr, LambdaExprASTContext context, Object paramVal) throws LambdaExprASTException {
        LambdaExprTokenParser parser = new LambdaExprTokenParser(expr);
        parser.getNextToken();
         return new LambdaFuncExprAST(context,parser,paramVal).getVal();
    }
}
