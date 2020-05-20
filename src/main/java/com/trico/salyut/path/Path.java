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

package com.trico.salyut.path;

import java.util.HashMap;
import java.util.Map;

public class Path {
	private  Map<String,Object> _PATH_VAR_MAP = new HashMap<String,Object>();
	public final static String INNER_VAR_PREFIX = "__";
	private final static String GLOBAL_PREFIX = "/_G";

	public enum Reversed{
		/** $0 返回{@link com.trico.salyut.token.SToken} 名 */
		R0(0, "0"),

		/** $1 返回{@link com.trico.salyut.token.SToken} 执行结果 可能为空 */
		R1(1, "1"),

		/** $e 返回{@link com.trico.salyut.token.Loop} 中每次循环中的 web element元素 */
		E(2, "e"),

		/** $e 返回{@link com.trico.salyut.token.Loop} 中每次循环中的 values数组中迭代值 */
		V(3, "v"),

		/** $e 返回{@link com.trico.salyut.token.Loop} 中每次循环中的 计数下标 */
		I(4, "i"),

		/** $e 返回{@link com.trico.salyut.token.Loop} 中循环总数 */
		COUNT(5, "count"),

		/** $true 相当与{@link Boolean#TRUE} */
		CONST_TRUE(6,"true"),

		/** $false 相当与{@link Boolean#FALSE} */
		CONST_FALSE(7,"false"),

		/** $now 获取当前系统毫秒数 */
		CONST_NOW(8,"now"),

		/** $null 相当于null */
		CONST_NULL(9,"null");

		int code;
		String value;
		Reversed(int code, String value){
			this.code = code;
			this.value = value;
		}

		public String getValue(){
			return value;
		}
	}


	public Map<String,Object> getStorage(){
		return _PATH_VAR_MAP;
	}


	public boolean isReserved(String rId){
		if (Reversed.R0.getValue().equals(rId)
				||Reversed.R1.getValue().equals(rId)
				||Reversed.E.getValue().equals(rId)
				||Reversed.V.getValue().equals(rId)
				||Reversed.I.getValue().equals(rId)
				||Reversed.COUNT.getValue().equals(rId)
				||Reversed.CONST_TRUE.getValue().equals(rId)
				||Reversed.CONST_FALSE.getValue().equals(rId)
				||Reversed.CONST_NOW.getValue().equals(rId)
				||Reversed.CONST_NULL.getValue().equals(rId)){
			return true;
		}

		return false;
	}

	public boolean isReservedConst(String rId){
		if (Reversed.CONST_TRUE.getValue().equals(rId)
				||Reversed.CONST_FALSE.getValue().equals(rId)
				||Reversed.CONST_NOW.getValue().equals(rId)
				||Reversed.CONST_NULL.getValue().equals(rId)){
			return true;
		}

		return false;
	}

	public static boolean isGlobalVar(String path){
		return path.startsWith(GLOBAL_PREFIX);
	}

	public Path(){
	}

}
