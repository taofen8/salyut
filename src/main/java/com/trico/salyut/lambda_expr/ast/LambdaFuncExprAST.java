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

import com.trico.salyut.lambda_expr.LambdaExprASTContext;
import com.trico.salyut.lambda_expr.LambdaExprTokenParser;

public class LambdaFuncExprAST extends LambdaExprAST {
    private String param;
    private String block;
    private Object paramVal;
    public LambdaFuncExprAST(LambdaExprASTContext context, LambdaExprTokenParser parser, Object paramVal) throws LambdaExprASTException {
        super(context,parser,paramVal);
    }

    @Override
    public void construct(Object... otherParams) throws LambdaExprASTException {
        this.paramVal = otherParams[0];
        while (true){
            if (parser.isEOF()) return;

            if (parser.isParam()){
                this.param = parser.getCurToken().str;
                parser.getNextToken();
            }

            if (parser.isPointer()){
                parser.getNextToken(); //eat ->
            }

            if (parser.isBlock()){
                this.block = parser.getCurToken().str;
                this.val = context.exec(rebuildFunc(),paramVal);
                parser.getNextToken();
            }
        }
    }

    private String rebuildFunc(){
        StringBuilder builder = new StringBuilder();
        builder.append("var "+param+" = arguments[0]; ");
        builder.append(block);
        return builder.toString();
    }
}
