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

import java.util.ArrayList;
import java.util.HashMap;

public class PutPathExprAST extends ExprAST {
    private Object putVal;
    private IndexExprAST index;
    public PutPathExprAST(ExprASTContext context, ExprTokenParser parser, Object putVal) throws ExprASTException{
        super(context,parser,putVal);
    }

    @Override
    public void construct(Object... otherParams) throws ExprASTException {
        this.putVal = otherParams[0];
        String label = "";
        while(true){
            if (parser.isEOF()) return;

            if (parser.isPath()){
                label =  parser.getCurToken().str;
                parser.getNextToken();
                if (parser.isEOF()){
                    context.setValue(val,label,null,putVal);
                }
                else{
                    ID tmpVal = context.getValue(val,label,null);
                    if (null == tmpVal || tmpVal.isNull()){
                        if (parser.isIdentifier()){
                            if (parser.getCurToken().str.equals("[")){
                                this.val = context.setValue(val,label,null,new ArrayList<>());
                                continue;
                            }
                        }
                        this.val = context.setValue(val,label,null,new HashMap());
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
                        context.setValue(val,label,index.getVal().castLong(),putVal);
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
