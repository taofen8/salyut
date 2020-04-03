package com.trico.salyut.token;

import com.trico.salyut.annotation.TokenMark;
import org.openqa.selenium.WebElement;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.exception.SalyutException;

@TokenMark(name = "upload")
public class UploadFile extends SToken{
	@Attribute(name = "ele",rebuildEle = true)
	private String ele = null;
	@Attribute(name = "value",exprScan = true)
	private Object value = null;
	
	@Override
	public void action() throws SalyutException {
		super.action();
		WebElement element = (WebElement) getExprValue(ele);
		element.sendKeys(value.toString());
		
	}

}
