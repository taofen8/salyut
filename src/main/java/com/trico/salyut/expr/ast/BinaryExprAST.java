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
 * BinaryExprAST
 * 表达式运算递归下降
 *
 * @author shenyin
 * */

public class BinaryExprAST extends  ExprAST {
    private ID leftVal;
    int exprPrec;
    public BinaryExprAST(ExprASTContext context, ExprTokenParser parser, Integer exprPrec, ID leftVal) throws  ExprASTException{
        super(context,parser,exprPrec,leftVal);
    }

    @Override
    public void construct(Object ... otherParams) throws ExprASTException{
        this.exprPrec = (int)otherParams[0];
        this.leftVal = (ID)otherParams[1];


        while(true){
            if (parser.isEOF()){
                return;
            }
            int tokPrec = parser.getCurToken().getPrec();
            if (tokPrec < exprPrec){
                this.val = leftVal;
                return;
            }

            String binOp = parser.getCurToken().str;
            parser.getNextToken();

            ExprAST right = super.primary(); //next token eat
            ID rightVal = right.getVal();

            int nextPrec = parser.getCurToken().getPrec();
            if (tokPrec < nextPrec){
                rightVal = new BinaryExprAST(context,parser,tokPrec+1,rightVal).getVal();
            }

            this.val = ID.calc(binOp,leftVal,rightVal);
            return;
        }
    }
}
