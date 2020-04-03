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

package com.trico.salyut.browser;

import com.trico.salyut.engine.ExecResult;

/**
 * 脚本执结果监听器
 *
 * @author Shen Yin
 * @since 0.0.8
 */
public interface ExecResultListener {
	/**
	 * 根据{@link ExecResult} 设置返回结果
	 *
	 * @param result
	 * 		{@link com.trico.salyut.STab} 执行结果
	 */
	void setResult(ExecResult result);
}
