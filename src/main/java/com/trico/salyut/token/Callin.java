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

package com.trico.salyut.token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.trico.salyut.Salyut;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

/**
 * <b>callin</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/callin/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "callin")
public class Callin extends SToken {
	@Attribute(name = "seg", exprScan = true)
	private String seg;
	@Attribute(name = "package", exprScan = true)
	private String packageName = "default";
	@Attribute(name = "args")
	private String args;

	@SuppressWarnings("unchecked")
	@Override
	public void action() throws SalyutException {
		super.action();

		List<Object> argList;
		Object argsObj = null;

		if (args != null){
			argsObj = getExprValue(args);
		}

		if (null == argsObj){
			Map<String, Object> map = (Map<String, Object>) getParam();
			argList = new ArrayList<>();
			Integer argIndex = 0;
			while (argIndex < 50) {
				String argPath = null;
				if ((argPath = (String) map.get(String.valueOf(argIndex))) == null) {
					if ((argPath = (String) map.get(argIndex)) == null) {
						break;
					}
				}
				argList.add(getExprValue(argPath));
				argIndex++;
			}
		}
		else{
			if (argsObj instanceof List){
				argList = ((List) argsObj);
			}
			else{
				throw new SalyutException(SalyutExceptionType.RuntimeError,this, "args must be `array` type");
			}
		}



		if (seg != null) {
			String segmentYaml = Salyut.segmentMap.get(segMapKey());
			if (null == segmentYaml){
				throw  new SalyutException(SalyutExceptionType.SegmentNotFound,this,"Can not find segment named \""+seg+"\"");
			}
			Segment segment = Segment.produceSelf(Salyut.segmentMap.get(segMapKey()), atTab);

			/**
			 * fix issue: when callin args pass null, will throw null pointer exception
			 * because of the segment has no chance to set a HashMap holding the vars.
			 */
			setExprValue(segment.getSegmentPath(), new HashMap<>(),true);

			for (int i = 0; i < argList.size() && i < segment.getArgs().size(); i++) {
				String path = segment.getArgs().get(i);
				setExprValue(path,argList.get(i),true);
			}

			ExecResult result = segment.invoke();
			if (ExecResult.InterruptType.RETURN == result.intType){
				setResult(result.val);
			}
			/** 当前函数调用不需要传递 {@link Segment}的{@link ExecResult} 只需要{@link ExecResult#getThrough()}即可 */
			setExecResult(ExecResult.getThrough());
		}
	}

	private String segMapKey(){
		return packageName+"_"+seg;
	}

	public static class TokenBuilder {
		private String seg;
		private List<String> args = new ArrayList<String>();
		private String packageName;

		public static TokenBuilder newBuilder() {
			return new Callin.TokenBuilder();
		}

		public TokenBuilder seg(String seg) {
			this.seg = seg;
			return this;
		}

		public TokenBuilder packageName(String packageName){
			this.packageName = packageName;
			return this;
		}

		public TokenBuilder addArg(String arg) {
			this.args.add(arg);
			return this;
		}
	}
	

}
