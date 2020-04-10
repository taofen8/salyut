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

package com.trico.salyut.exception;

/**
 * Salyut异常枚举类型
 *
 * @author sheny
 */
public enum SalyutExceptionType {
    /**
	 * @deprecated
	 * 数值错误
	 */
	ValueError(0,"[value error]:"),

	/**
	 * @deprecated
	 * 类型错误
	 */
	TypeError(1,"[type error]:"),

	/**
	 * @deprecated
	 * 属性错误
	 * 使用 {@link SalyutExceptionType#ParseError} 替代
	 */
	AttributeError(2,"[attribute error]:"),

	/** 属性解析出错 */
	ParseError(3,"[parse error]:"),

	/**
	 * @deprecated
	 * 节点未找到
	 */
	EleNotFound(4,"[element not found]:"),

	/**
	 * @deprecated
	 * 方法未找到
	 */
	FuncNotFound(5,"[function not found]:"),

	/** 运行时错误 */
	RuntimeError(6,"[runtime error]:"),

	/**
	 * @deprecated
	 * 用于return返回，不再使用
	 */
	Return(7,"[]"),

	/**
	 * selenium错误
	 */
	SeleniumError(8,"[selenium error]:"),

	/** 通过exception强行终止任务  */
	Stop(9,"[stop]"),

	/** 表达式语法树错误  */
	ExprASTError(10,"[expr ast error]"),

	/** Lambda表达式语法树错误  */
	LambdaExprASTError(11,"[lambda expr ast error]"),

	/** segment未找到  */
	SegmentNotFound(12,"[segment not found]"),

	/** 未预料的错误  */
	UnhandledError(13,"[unhandled error]"),

	/** 越界  */
	OutOfBounds(13,"[out of bounds error]"),

	/** 通过exception切换成手机浏览器模式  */
	MobileMode(14,"[mobile mode]"),

	DeadLoop(15,"[dead loop]");
	
	private int code;
	private String explain;
	
	public boolean equals(SalyutExceptionType other){
		return this.code == other.code;
	}
	
	public int getCode() {
		return code;
	}

	public String getExplain() {
		return explain;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public void setExplain(String explain) {
		this.explain = explain;
	}

	SalyutExceptionType(int code,String explain){
		this.code = code;
		this.explain = explain;
	}
}
