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
import com.trico.salyut.expr.ID;

/**
 * StrExprAST
 *
 * @author shenyin
 * */
public class StrExprAST extends ExprAST{
    public StrExprAST(ExprASTContext context, ExprTokenParser parser) throws ExprASTException{
        super(context,parser);
    }

    @Override
    public void construct(Object ... otherParams) {

        this.val = new ID(parser.getCurToken().str);
        parser.getNextToken();
    }
}
