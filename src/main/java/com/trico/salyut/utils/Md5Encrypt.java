package com.trico.salyut.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/** 
* 功能：支付宝MD5加密处理核心文件，不需要修改
* 版本：3.1
* 修改日期：2010-11-01
* 说明：
* 以下代码只是为了方便商户测试而提供的样例代码，商户可以根据自己网站的需要，按照技术文档编写,并非一定要使用该代码。
* 该代码仅供学习和研究支付宝接口使用，只是提供一个
* */

public class Md5Encrypt {
    /**
     * Used building output as Hex
     */
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * 对字符串进行MD5加密
     * 
     * @param text
     *            明文
     * 
     * @return 密文
     */
    public static String md5(String text, String charset) {
        MessageDigest msgDigest = null;

        try {
            msgDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "System doesn't support MD5 algorithm.");
        }

        try {
            msgDigest.update(text.getBytes(charset));    //注意改接口是按照指定编码形式签名
 
        } catch (UnsupportedEncodingException e) {
           
            throw new IllegalStateException(
                    "System doesn't support your  EncodingException.");

        }

        byte[] bytes = msgDigest.digest();

        String md5Str = new String(encodeHex(bytes));

        return md5Str;
    }

    public static char[] encodeHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];

        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return out;
    }
    
    
    /**
     * 常规md5加密
     * 
     * @param text
     *            明文
     * 
     * @return 密文
     * @throws UnsupportedEncodingException 
     */
    public static byte[] md5Digest(String text, String charset) throws UnsupportedEncodingException {
		MessageDigest messageDigest = null;

        try {
        	messageDigest = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "System doesn't support MD5 algorithm.");
        }
        
		byte[] byteArray;
		try {
			byteArray = messageDigest.digest(text.getBytes(charset));
		} catch (UnsupportedEncodingException e) {
	        throw new IllegalStateException(
                    "System doesn't support your  EncodingException.");
		}
	
		return byteArray;
    }
    
    public static String getMd5ByFile(File file) throws FileNotFoundException {  
    	MessageDigest md5 = null;  
        FileInputStream in = new FileInputStream(file);  
	    try {  
	        MappedByteBuffer byteBuffer = in.getChannel().map(FileChannel.MapMode.READ_ONLY, 0, file.length());  
	        md5 = MessageDigest.getInstance("MD5");  
	        md5.update(byteBuffer);  

	    } catch (Exception e) {  
	        e.printStackTrace();  
	    } finally {  
	            if(null != in) {  
	                try {  
	                in.close();  
	            } catch (IOException e) {  
	                e.printStackTrace();  
	            }  
	        }  
	    }  
	    byte[] bytes = md5.digest();
	    
	    String md5Str = new String(encodeHex(bytes));
	    return md5Str;  
    }
    
    public static void main(String args[]){
    	System.out.println(md5("http://www.zappos.com/images/z/3/8/6/3/4/9/3863496-6-2x.jpg", "utf-8"));
    }
}