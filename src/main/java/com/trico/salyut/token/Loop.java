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

import java.util.List;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.engine.ExecResult;
import com.trico.salyut.exception.Check;
import com.trico.salyut.exception.SalyutExceptionType;
import com.trico.salyut.expr.ID;
import com.trico.salyut.path.Path;
import com.trico.salyut.exception.SalyutException;
import org.openqa.selenium.WebElement;

/**
 * <b>loop</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/loop/index.html</a>
 *
 * @author shenyin
 */
@TokenMark(name = "loop")
public class Loop extends SToken implements Block{
	@Deprecated
	@Attribute(name = "eles",underKey = "in", rebuildEle = true)
	private String eles = null;
	@Attribute(name = "targets",underKey = "in", isSel = true)
	private String targets = null;
	@Attribute(name = "parent", underKey = "in", isSel = true)
	private String parent = null;
	@Attribute(name = "values",underKey = "in",exprScan = true)
	private Object values = null;
	private List<SToken> tokenList;
	private int eleIndex = 0;
	@Attribute(name = "start",underKey = "in",exprScan = true)
	private Long start = 0L;
	@Attribute(name = "end",underKey = "in",exprScan = true)
	private Long end = -1L;
	@Attribute(name = "step",underKey = "in",exprScan = true)
	private Long step = 1L;
	@Attribute(name = "mutable",underKey = "in", exprScan = true)
	private boolean mutable = true;
	@Attribute(name = "cond", underKey = "in")
	private String cond = null;

	@Override
	public void action() throws SalyutException {
		super.action();
		if (targets != null){
			setExecResult(targetsLoop());
		}
		else if (eles != null){
			setExecResult(elesLoop());
		}
		else if (values != null){
			setExecResult(valuesLoop());
		}
		else if (cond != null){
			setExecResult(condLoop());
		}
		else {
			setExecResult(countLoop());
		}
	}

	private ExecResult targetsLoop() throws SalyutException{
		if (parent != null){
			setSubFinder(getWebElementByExpr(parent));
		}

		List<WebElement> webElements = getWebElementsByExpr(targets);
		int lastElementsCount = webElements.size();
		setExprValue(Path.Reversed.COUNT, lastElementsCount);

		for (int i = start.intValue(); i < webElements.size() && (-1L == end || i < end); i+=step) {
			WebElement webElement = webElements.get(i);
			setExprValue(Path.Reversed.E, webElement);
			setExprValue(Path.Reversed.I, i);
			ExecResult result;
			try{
				result = atTab.executor.execute(statements);
			}
			catch (SalyutException e){
				throw new SalyutException(SalyutExceptionType.RuntimeError,e.getToken(),e.getAddonMessage()+ " at loop times "+i);
			}

			if (result.intType.equals(ExecResult.InterruptType.BREAK)){
				return ExecResult.getThrough();
			}
			else if (result.intType.equals(ExecResult.InterruptType.RETURN)){
				return result;
			}

			if (mutable) {
				findMultiElesOn();
				webElements = getWebElementsByExpr(targets);
				findMultiElesOff();
				if (webElements.size() < lastElementsCount) { //如果指令会删除节点，则把当前节点的指令全部执行完毕，并将eleIndex归零
					i--;
				}
			}

		}

		return ExecResult.getThrough();
	}

	@Deprecated
	private ExecResult elesLoop() throws SalyutException{
		if (parent != null){
			setSubFinder(getWebElementByExpr(parent));
		}

		findMultiElesOn();
		List<WebElement> webElements = (List<WebElement>) getExprValue(eles);
		findMultiElesOff();
		int lastElementsCount = webElements.size();
		setExprValue(Path.Reversed.COUNT, lastElementsCount);
		for (int i = start.intValue(); i < webElements.size() && (-1L == end || i < end); i+=step) {
			WebElement webElement = webElements.get(i);
			setExprValue(Path.Reversed.E, webElement);
			setExprValue(Path.Reversed.I, i);
			ExecResult result;
			try{
				result = atTab.executor.execute(statements);
			}
			catch (SalyutException e){
				throw new SalyutException(SalyutExceptionType.RuntimeError,e.getToken(),e.getAddonMessage()+ " at loop times "+i);
			}

			if (result.intType.equals(ExecResult.InterruptType.BREAK)){
				return ExecResult.getThrough();
			}
			else if (result.intType.equals(ExecResult.InterruptType.RETURN)){
				return result;
			}


			if (mutable) {
				findMultiElesOn();
				webElements = (List<WebElement>) getExprValue(eles);
				findMultiElesOff();
				if (webElements.size() < lastElementsCount) { //如果指令会删除节点，则把当前节点的指令全部执行完毕，并将eleIndex归零
					i--;
				}
			}

		}

		return ExecResult.getThrough();
	}

	private ExecResult valuesLoop() throws SalyutException {
		if (values != null) {
			Check.requireListType(values,this);
			setExprValue(Path.Reversed.COUNT, ((List) values).size());
			for (int i = start.intValue(); i < ((List) values).size() && (-1L == end || i < end); i+=step) {
				setExprValue(Path.Reversed.V, ((List) values).get(i));
				setExprValue(Path.Reversed.I, i);
				ExecResult result;
				try{
					result = atTab.executor.execute(statements);
				}
				catch (SalyutException e){
					throw new SalyutException(SalyutExceptionType.RuntimeError,e.getToken(),e.getAddonMessage()+ " at loop times "+i);
				}
				if (result.intType.equals(ExecResult.InterruptType.BREAK)){
					return ExecResult.getThrough();
				}
				else if (result.intType.equals(ExecResult.InterruptType.RETURN)){
					return result;
				}
			}
		}
		return ExecResult.getThrough();
	}

	private ExecResult condLoop() throws SalyutException{
		int i = 0;
		while(new ID(getExprValue(cond)).castBool()) {
			setExprValue(Path.Reversed.I, i);
			i++;
			ExecResult result;
			try{
				result = atTab.executor.execute(statements);
			}
			catch (SalyutException e){
				throw new SalyutException(SalyutExceptionType.RuntimeError,e.getToken(),e.getAddonMessage()+ " at loop times "+i);
			}
			if (result.intType.equals(ExecResult.InterruptType.BREAK)){
				return ExecResult.getThrough();
			}
			else if (result.intType.equals(ExecResult.InterruptType.RETURN)){
				return result;
			}
		}
		return ExecResult.getThrough();
	}

	private ExecResult countLoop() throws SalyutException{
		setExprValue(Path.Reversed.COUNT,end);
		for(int i = start.intValue(); i < end; i+=step){
			setExprValue(Path.Reversed.E,null);
			setExprValue(Path.Reversed.I,i);
			ExecResult result;
			try{
				result = atTab.executor.execute(statements);
			}
			catch (SalyutException e){
				throw new SalyutException(SalyutExceptionType.RuntimeError,e.getToken(),e.getAddonMessage()+ " at loop times "+i+".");
			}
			if (result.intType.equals(ExecResult.InterruptType.BREAK)){
				return ExecResult.getThrough();
			}
			else if (result.intType.equals(ExecResult.InterruptType.RETURN)){
				return result;
			}
		}

		return ExecResult.getThrough();
	}

	@Override
	public String key() {
		return "each";
	}
}
