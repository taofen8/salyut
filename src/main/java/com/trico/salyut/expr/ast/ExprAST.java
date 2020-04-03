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

public abstract  class ExprAST {
    protected ExprASTContext context;
    protected ExprTokenParser parser;
    protected ID val;
    public ExprAST(ExprASTContext context,ExprTokenParser parser, Object ... otherParams) throws ExprASTException{
        this.context = context;
        this.parser = parser;
        construct(otherParams);
    }

    public ID getVal() throws ExprASTException{
        return val;
    }

    public abstract void construct(Object ... otherParams) throws ExprASTException;

    ExprAST primary() throws ExprASTException{
        while(true){
            if (parser.isIdentifier()){
                if (parser.getCurToken().str.equals("$")){
                    return new VariableExprAST(context,parser,true);
                }
                else if (parser.getCurToken().str.equals("(") || parser.getCurToken().str.equals(")")){
                    parser.getNextToken(); //eat ()
                }
            }
            else if (parser.isNumInt() || parser.isNumDouble()){
                return new NumExprAST(context,parser);
            }
            else if (parser.isStr()){
                return new StrExprAST(context,parser);
            }
            else if (parser.isCssSelector()){
                return new CssSelectorExprAST(context,parser);
            }
            else{
                throw new ExprASTException("unknown token when expecting an expression");
            }

        }

    }
}
