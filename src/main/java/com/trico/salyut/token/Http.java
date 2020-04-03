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

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import com.trico.salyut.annotation.Attribute;
import com.trico.salyut.annotation.TokenMark;
import com.trico.salyut.exception.SalyutException;
import com.trico.salyut.exception.SalyutExceptionType;

import okhttp3.*;

/**
 * <b>http</b>
 *
 * <p>docs link:
 * <a>https://www.trico.cloud/tricoDoc/htpp/index.html</a>
 * @author shenyin
 */
@TokenMark(name = "http")
public class Http  extends SToken {
	private final static  OkHttpClient httpClient = new OkHttpClient().newBuilder()
			.followRedirects(true)
			.readTimeout(60, TimeUnit.SECONDS)
			.writeTimeout(60, TimeUnit.SECONDS)
			.build();
	@Attribute(name = "url",exprScan = true, required = true)
	private String url = null;
	@Attribute(name = "post", exprScan = true)
	private String post = null;
	@Attribute(name = "media", exprScan = true)
	private String media = null;
	@Attribute(name = "headers", exprScan = true)
	private Map<String,String> headers = null;
	@Attribute(name = "async", exprScan = true)
	private boolean async = false;
	@Attribute(name = "path")
	private String path= null;

	
	@Override
	public void action() throws SalyutException {
		super.action();
		try {
			Request request;

			if (headers != null && !(headers instanceof Map)){
				throw new SalyutException(SalyutExceptionType.RuntimeError,this,"headers must be a map type.");
			}
			Map<String,String> wrapperHeaders = new HashMap<>();
			if (headers != null){
				for (String key:headers.keySet()){
					wrapperHeaders.put(key.replace("_","-"),headers.get(key));
				}
			}

			if (null == post){
				request = new Request.Builder()
						.url(url)
						.headers(Headers.of(wrapperHeaders))
						.build();
			}
			else{
				request = new Request.Builder()
						.url(url)
						.headers(Headers.of(wrapperHeaders))
						.post(RequestBody.create(createMediaType(media,"charset=utf-8"),post))
						.build();
			}

			if (async){
				httpClient.newCall(request).enqueue(new Callback() {
					@Override
					public void onFailure(Call call, IOException e) {

					}

					@Override
					public void onResponse(Call call, Response response) throws IOException {
						try{
							if (null != path){
								setExprValue(path,parseResponse(response));
							}

						}catch (SalyutException e){}

					}
				});
			}
			else{
				if (null != path){
					setExprValue(path, parseResponse(httpClient.newCall(request).execute()));
				}
				else{
					httpClient.newCall(request).execute();
				}

			}
		}catch (Exception e){
			throw new SalyutException(SalyutExceptionType.RuntimeError,this,"error:"+e.getMessage());
		}
	}

	private static Object parseResponse(Response response) throws IOException{
		String type = response.body().contentType().type();
		String subType = response.body().contentType().subtype();
		if (null == type
				|| type.startsWith("text")
				|| "json".equals(subType)){
			return response.body().string();
		}
		else {
			return response.body().bytes();
		}
	}

	private static MediaType createMediaType(String media, String charset){
		if (null == media|| media.length() == 0){
			return MediaType.parse("text/plain; "+charset);
		}
		return MediaType.parse(media+"; "+charset);
	}

}
