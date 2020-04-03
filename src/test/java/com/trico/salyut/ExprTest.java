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

package com.trico.salyut;

import com.trico.salyut.expr.ExprASTContext;
import com.trico.salyut.expr.ExprTokenParser;
import com.trico.salyut.expr.ID;
import com.trico.salyut.expr.ast.*;
import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ExprTest extends TestCase {
    public static Test suite()
    {
        return new TestSuite( ExprTest.class );
    }

    public static class TestExprASTContext implements ExprASTContext {
        private Map map = new HashMap<String,Object>();
        @Override
        public ID getValue(ID base, String label, Long index) {
            if (base == null){
                return new ID(map.get(label));
            }
            else{
                if (index != null){
                    return new ID(((List)(base.val())).get(index.intValue()));
                }
                else{
                    return new ID(((Map)base.val()).get(label));
                }

            }
        }

        @Override
        public boolean isGlobalVar(String path) {
            return false;
        }

        @Override
        public ID segmentScopeVal() {
            return null;
        }

        @Override
        public ID setValue(ID base, String label, Long index, Object val) {
            if (base == null){
                map.put(label,val);
            }
            else{
                if (index != null){
                    if (index == -1){
                        ((List) base.val()).add(val);
                    }
                    else{
                        ((List) base.val()).add(index.intValue(),val);
                    }
                }
                else{
                    ((Map) base.val()).put(label,val);
                }
            }

            return new ID(val);
        }

        @Override
        public void removeValue(ID base, String label, Long index) {
            if (base == null){
                map.remove(label);
            }
            else{
                if (index != null){
                    ((List)(base.val())).remove(index.intValue());
                }
                else{
                    ((Map)base.val()).remove(label);
                }
            }
        }

        @Override
        public ID getWebElement(String cssSelector) {
            return null;
        }

        @Override
        public ID getReservedValue(String rId) {
            if (rId.equals("true")){
                return new ID(true);
            }
            return null;
        }

        public Map getMap() {
            return map;
        }
    }

    public void testMainLoop() throws ExprASTException {
        TestExprASTContext context = new TestExprASTContext();
        List<Integer> list = new ArrayList<>();
        list.add(1);
        list.add(3);
        list.add(3);
        list.add(4);
        list.add(5);
        list.add(6);

        String putExpr = "/";
        ExprTokenParser putParser = new ExprTokenParser(putExpr);
        putParser.getNextToken();
        PutPathExprAST putPathExprAST = new PutPathExprAST(context,putParser,list);

//        putExpr = "/root/arr[-1]";
//        putParser = new ExprTokenParser(putExpr);
//        putParser.getNextToken();
//        putPathExprAST = new PutPathExprAST(context,putParser,7);

        String expr = "$true";
        ExprTokenParser parser = new ExprTokenParser(expr);
        ExprAST lastAST = null;
        parser.getNextToken();
        while (!parser.isEOF()){
            if (parser.isIdentifier()){
                if (parser.getCurToken().str.equals("$")){
                    lastAST = new VariableExprAST(context,parser,false);
                }
                else if (parser.getCurToken().str.equals("(") || parser.getCurToken().str.equals(")")){
                    parser.getNextToken(); //eat ()
                }
                else{
                    parser.getNextToken();
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
        }

        System.out.println(lastAST.getVal().val());
    }
}
