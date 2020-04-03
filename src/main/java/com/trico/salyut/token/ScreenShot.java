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

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;

@TokenMark(name = "screenshot")
public class ScreenShot extends SToken {
	
	@Override
	public void action() throws SalyutException {
		// TODO Auto-generated method stub
		super.action();
		
		  File screenShotFile = this.atTab.driver.getScreenshotAs(OutputType.FILE);
		  try {
			FileUtils.copyFile(screenShotFile, new File("/Users/hanjiahu/desktop/" + getCurrentDateTime()+ ".jpg"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		  
	}
	
	public static String getCurrentDateTime(){
		SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd_HHmmss");//设置日期格式
		//System.out.println(df.format(new Date()));
		return df.format(new Date());
	}
	
}