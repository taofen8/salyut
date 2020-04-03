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

import com.trico.salyut.expr.ExprASTContext;
import com.trico.salyut.expr.ExprTokenParser;

public class CssSelectorExprAST extends ExprAST {
    public CssSelectorExprAST(ExprASTContext context, ExprTokenParser parser) throws ExprASTException{
        super(context,parser);
    }
    @Override
    public void construct(Object... otherParams) throws ExprASTException {
        this.val = context.getWebElement(parser.getCurToken().str);
        parser.getNextToken();
    }
}
