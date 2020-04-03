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

import com.trico.salyut.expr.ast.ExprASTException;

/**
 * ExprASTContext
 * 该接口用来代理访问Salyut引擎中所有对象的上下文
 *
 * base 当前层级对象
 * label 当前层级Key
 * index 如果当前层级对象为List列表，可以使用index下标来访问
 *
 * @author shenyin
 * */
public interface ExprASTContext {
    ID getValue(ID base,String label,Long index);
    void removeValue(ID base,String label,Long index);
    ID segmentScopeVal() throws ExprASTException; //获取基于segment的变量空间
    boolean isGlobalVar(String path);
    ID setValue(ID base,String label,Long index, Object val);
    ID getReservedValue(String rId) throws ExprASTException;
    ID getWebElement(String cssSelector) throws ExprASTException;
}
