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

/**
 * IndexExprAST
 * 下标表达式虚拟语法树
 *
 * {@link NumExprAST}
 * {@link StrExprAST}
 * {@link VariableExprAST}
 *
 * @author shenyin
 */

public class IndexExprAST extends ExprAST {
    private ExprAST lastAST;

    public IndexExprAST(ExprASTContext context, ExprTokenParser parser) throws ExprASTException{
        super(context,parser);
    }

    @Override
    public void construct(Object ... otherParams) throws ExprASTException{
        parser.getNextToken(); //eat [
        while(true){
            if (parser.isEOF()) return;

            if (parser.isNumInt()){
                lastAST = new NumExprAST(context,parser);
            }
            if (parser.isNumDouble()){
                lastAST = new NumExprAST(context,parser);
            }
            if (parser.isStr()){
                lastAST = new StrExprAST(context,parser);
            }
            if (parser.isOperator()){
                lastAST = new BinaryExprAST(context,parser,0,lastAST == null ? null : lastAST.getVal());
            }

            if (parser.isIdentifier()){
                if (parser.getCurToken().str.equals("$")){
                    lastAST = new VariableExprAST(context,parser,true);
                }

                if (parser.getCurToken().str.equals("(") || parser.getCurToken().str.equals(")")){
                    parser.getNextToken(); //eat ()
                }

                if (parser.getCurToken().str.equals("]")){
                    if (lastAST == null){
                        throw new IndexExprASTException("Do you miss -1 for appending item to an array?");
                    }
                    if (lastAST instanceof StrExprAST){
                        throw new IndexExprASTException("index value must be int type");
                    }
                    else{
                        this.val = lastAST.getVal();
                        parser.getNextToken(); //eat ]
                    }

                    return;
                }
            }

        }

    }

    public static class IndexExprASTException extends ExprASTException{
        IndexExprASTException(String message) {
            super(message);
        }
    }
}
