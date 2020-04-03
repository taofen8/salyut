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
 * VariableExprAST
 * 变量虚拟语法数
 *
 * 处理
 * $/path
 * $/path[0]
 * 其中包含 {@link IndexExprAST}
 *
 * @author shenyin
 * */

public class VariableExprAST extends ExprAST{
    private IndexExprAST index;
    private Boolean considerSegScope;
    public VariableExprAST(ExprASTContext context, ExprTokenParser parser, Boolean considerSegScope) throws ExprASTException{
        super(context,parser,considerSegScope);
    }

    @Override
    public void construct(Object ... otherParams) throws ExprASTException{
        parser.getNextToken(); //eat $
        this.considerSegScope = ((Boolean) otherParams[0]);

        //check是否为全局变量，如果是全局变量不需要走segment栈变量的逻辑
        boolean isGlobal = parser.isPath() && context.isGlobalVar(parser.getCurToken().str);
        if (!isGlobal && considerSegScope){
            //需要基于segment栈
            this.val = context.segmentScopeVal();
        }

        String label = "";
        while(true){
            if (parser.isEOF()) {
                return;
            }

            if (parser.isPath()){
                label =  parser.getCurToken().str;
                this.val = context.getValue(val,label,null);
                parser.getNextToken();
            }
            else if (parser.isReversed()){
                this.val = context.getReservedValue(parser.getCurToken().str);
                parser.getNextToken();
            }
            else if (parser.isIdentifier()){
                 if (parser.getCurToken().str.equals("[")){
                    this.index = new IndexExprAST(context,parser);
                    this.val = context.getValue(val,label,index.getVal().castLong());
                }
                else {
                    return;
                }
            }
            else if (parser.isUndefined()){
                throw new ExprASTException("can not parse undefined token:"+parser.getCurToken().str);
            }
            else{
                return;
            }
        }
    }
}
