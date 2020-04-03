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
 * NumExprAST
 *
 * @author shenyin
 * */
public class NumExprAST extends ExprAST {
    public NumExprAST(ExprASTContext context, ExprTokenParser parser) throws ExprASTException{
        super(context,parser);
    }

    @Override
    public void construct(Object ... otherParams) {
        if (parser.isNumInt()) {
            this.val = new ID(parser.getCurToken().numInt);
        } else if (parser.isNumDouble()) {
            this.val = new ID(parser.getCurToken().numDouble);
        }
        parser.getNextToken();
    }
}
