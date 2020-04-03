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

import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;

/**
 * @deprecated
 * <b>token switchtemptab</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/switchtemptab/index.html</a>
 * @author shen yin
 */
@TokenMark(name = "switchtemptab")
public class SwitchTempTab extends SToken{
	@Attribute(name = "index",exprScan = true)
	private Long index;

	@Override
	public void action() throws SalyutException {
		super.action();
		this.atTab.getBrowser().switchTab(index);;
	}
	
}
