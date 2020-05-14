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
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

import org.openqa.selenium.WebElement;

/**
 * <b>select</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/select/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "select")
public class Select extends SToken {
	@Attribute(name = "target", isSel = true)
	private String target = null;
	@Attribute(name = "targets", isSel = true)
	private String targets = null;
	@Deprecated
	@Attribute(name = "ele",rebuildEle = true)
	private String ele = null;
	@Deprecated
	@Attribute(name = "eles",rebuildEle = true)
	private String eles = null;
	@Attribute(name = "path")
	private String path = null;
	@Attribute(name = "attr", exprScan = true)
	private String attr = null;
	@Attribute(name = "regex", exprScan = true)
	private String regex = null;
	@Deprecated
	@Attribute(name = "under",rebuildEle = true)
	private String under = null;
	@Attribute(name = "parent", isSel = true)
	private String parent = null;
	@Attribute(name = "icase")
	private boolean icase = false;

	@SuppressWarnings("unchecked")
	@Override
	public void action() throws SalyutException {
		super.action();
		if (parent != null){
			setSubFinder(getWebElementByExpr(parent));
		}
		else{
			if (under != null){
				setSubFinder((WebElement) getExprValue(under));
			}
		}

		if (target != null){
			String text = getText(getWebElementByExpr(target));
			setResult(text);
			setExprValue(path,text);
		}
		else if (targets != null){
			List<WebElement> webElementList = getWebElementsByExpr(targets);
			List<String> texts = new ArrayList<>();
			for (WebElement e:webElementList){
				texts.add(getText(e));
			}

			setExprValue(path,texts);
		}
		else{
			if (ele != null){
				WebElement webElement = (WebElement) getExprValue(ele);

				if (webElement == null){
					throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not find the element");
				}

				String text = getText(webElement);
				setResult(text);
				setExprValue(path,text);
			}
			else if (eles != null){
				findMultiElesOn();
				List<WebElement> webElements = (List<WebElement>) getExprValue(eles);
				findMultiElesOff();
				if (webElements == null){
					throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not find the elements");
				}

				List<String> texts = new ArrayList<>();
				for (WebElement e:webElements){
					texts.add(getText(e));
				}

				setExprValue(path,texts);
			}
		}

	}


	private String getText(WebElement webElement){
		String text = this.attr != null ? webElement.getAttribute(this.attr) : webElement.getText();
		if (text != null){
			if (text.length() == 0){
				text = webElement.getAttribute("innerText");
			}
			if (regex != null){
				Pattern p;
				if (icase){
					p = Pattern.compile(regex,Pattern.CASE_INSENSITIVE);
				}
				else{
					p = Pattern.compile(regex);
				}
				Matcher m = p.matcher(text);
				if(m.find()) {
					text = m.group(1);
				}
				else {
					text = null;
				}
			}
		}

		return text;
	}


}
