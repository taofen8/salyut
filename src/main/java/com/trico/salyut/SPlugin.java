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

import java.io.File;

import org.openqa.selenium.firefox.FirefoxProfile;

/**
 * 加载firefox插件
 */
public class SPlugin {
	public static FirefoxProfile profile = new FirefoxProfile();
	public static void newPlugin(String filePath){
		profile.addExtension(new File(filePath));
	}
	
	
}
