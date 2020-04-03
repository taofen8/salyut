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

package com.trico.salyut.expr;

import com.trico.salyut.expr.ast.*;

public class ExprMainLoop {
    public static ID getReversed(String expr,ExprASTContext context) throws ExprASTException {
        ExprTokenParser parser = new ExprTokenParser(expr);
        parser.getNextToken();
        return new VariableExprAST(context,parser,false).getVal();
    }

    public static ID get(String expr,ExprASTContext context) throws ExprASTException{
        ExprTokenParser parser = new ExprTokenParser(expr);
        ExprAST lastAST = null;
        parser.getNextToken();
        while (!parser.isEOF()){
            if (parser.isIdentifier()){
                if (parser.getCurToken().str.equals("$")){
                    lastAST = new VariableExprAST(context,parser,true);
                }
                else if (parser.getCurToken().str.equals("(") || parser.getCurToken().str.equals(")")){
                    parser.getNextToken(); //eat ()
                }
                else{
                    throw new ExprASTException("Unknown identifier, do you miss \"\" for input string?");
                }
            }
            else if (parser.isOperator()){
                lastAST = new BinaryExprAST(context,parser,0,lastAST == null ? null:lastAST.getVal());
            }
            else if (parser.isNumDouble() || parser.isNumInt()){
                lastAST = new NumExprAST(context,parser);
            }
            else if (parser.isStr()){
                lastAST = new StrExprAST(context,parser);
            }
            else if (parser.isCssSelector()){
                lastAST = new CssSelectorExprAST(context,parser);
            }
            else if (parser.isUndefined()){
                throw new ExprASTException("Unknown identifier, do you miss ending \" for input string?");
            }
        }

        if (null == lastAST){
            throw new ExprASTException("Create AST failed.");
        }
        else{
            return lastAST.getVal();
        }
    }

    public static void set(String expr,ExprASTContext context, Object val) throws ExprASTException{
        ExprTokenParser putParser = new ExprTokenParser(expr);
        putParser.getNextToken();
        new PutPathExprAST(context,putParser,val);
    }

    public static void remove(String expr,ExprASTContext context) throws ExprASTException{
        ExprTokenParser removeParser = new ExprTokenParser(expr);
        removeParser.getNextToken();
        new RemovePathExprAST(context,removeParser);
    }

}
