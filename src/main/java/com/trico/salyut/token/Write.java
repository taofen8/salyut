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
import java.io.FileOutputStream;
import java.io.OutputStream;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

import okhttp3.ResponseBody;

/**
 * <b>write</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/write/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "write")
public class Write extends SToken {
	@Attribute(name = "fpath", exprScan = true)
	private String fpath = null;
	@Attribute(name = "value",exprScan = true)
	private Object value = null;
	@Attribute(name = "append",exprScan = true)
	private boolean append = false;
	
	@Override
	public void action() throws SalyutException {
		super.action();
		File file = new File(fpath);
		try {
			 OutputStream  outputStream = new FileOutputStream(file,append);
			 if (value instanceof String){
			 	outputStream.write(((String)value).getBytes());
			 }
			 else if (value instanceof byte[]){
				 outputStream.write((byte[]) value);
			 }
			 else {
				 throw new SalyutException(SalyutExceptionType.RuntimeError,this,"can not resolve value type:"+value.getClass());
			 }
		     outputStream.flush();
			 outputStream.close(); 
		     
			 
		} catch (Exception e) {
			throw new SalyutException(SalyutExceptionType.RuntimeError,this,"error:"+e.getMessage());
		}
	}
	
	
	  private static void delete(String fileName) {
	        File file = new File(fileName);
	        if (file.isFile()) {
	            file.delete();
	           
	        }
	    }
	
	
		
}
