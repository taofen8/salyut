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

public class RemovePathExprAST extends ExprAST {
    private IndexExprAST index;
    public RemovePathExprAST(ExprASTContext context, ExprTokenParser parser) throws ExprASTException{
        super(context,parser);
    }

    @Override
    public void construct(Object... otherParams) throws ExprASTException {
        String label = "";
        while(true){
            if (parser.isEOF()) return;

            if (parser.isPath()){
                label =  parser.getCurToken().str;
                parser.getNextToken();
                if (parser.isEOF()){
                    context.removeValue(val,label,null);
                }
                else{
                    ID tmpVal = context.getValue(val,label,null);
                    if (null == tmpVal || tmpVal.isNull()){
                        throw new ExprASTException("value at path:"+label+ "is null");
                    }
                    else{
                        this.val = tmpVal;
                    }
                }
            }
            else if (parser.isIdentifier()){
                if (parser.getCurToken().str.equals("[")){
                    this.index = new IndexExprAST(context,parser);
                    if (parser.isEOF()){
                        context.removeValue(val,label,index.getVal().castLong());
                    }
                    else{
                        this.val = context.getValue(val,label,index.getVal().castLong());
                        if (this.val == null || this.val.isNull()){
                            throw new ExprASTException("value at path:"+label+ "is null");
                        }
                    }
                }
                else {
                    return;
                }
            }
            else{
                return;
            }
        }
    }
}
