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

package com.trico.salyut.struct;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TricoScript {
	private String yaml;
	private String content;
	
	public String getContent(){
		return this.content;
	}
	
	public TricoScript(String yaml){
		this.yaml = yaml;
		this.rebuildWithNo();
	} 
	
	private void rebuildWithNo(){
		StringBuilder builder = new StringBuilder();
		String[] lines = this.yaml.split("\n");
		for (int i = 0; i < lines.length; i++){
			builder.append(lineConvert(lines[i],i+1)+"\n");
		}
		
		this.content = builder.toString();
	}
	
	private  String lineConvert(String line,int no){
		Pattern p = Pattern.compile("([a-z|0-9|_]+):");
		Matcher m = p.matcher(line);
		String token = null;
		if(m.find()) token = m.group(1);
		if (token == null) return line;
		return line.replaceFirst(token, token+"_"+String.valueOf(no));
	}
	
	public TricoScript insertAtTop(String tokenString){
		if (tokenString.charAt(this.yaml.length()-1) != '\n'){
			this.yaml = tokenString+"\n"+ this.yaml;
		}
		else{
			this.yaml = tokenString + this.yaml;
		}
		
		this.rebuildWithNo();
		
		return this;
	}
	
	public TricoScript insertAtBottom(String tokenString){
		if (this.yaml.charAt(this.yaml.length()-1) != '\n'){
			this.yaml += "\n";
		}
	
		this.yaml += tokenString;
		
		if (this.yaml.charAt(this.yaml.length()-1) != '\n'){
			this.yaml += "\n";
		}
		
		this.rebuildWithNo();
		return this;
	}
	
}
