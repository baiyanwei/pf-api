package com.secpro.platform.api.IAPIServer.http;

import org.jboss.netty.handler.codec.http.HttpRequest;

import com.secpro.platform.api.IAPIServer.IHandler;

/**
 * @author baiyanwei HTTP request handler
 */
public interface IHttpHandler extends IHandler {
	//
	public Object DELETE(HttpRequest request, Object messageObj) throws Exception;

	public Object HEAD(HttpRequest request, Object messageObj) throws Exception;

	public Object OPTIONS(HttpRequest request, Object messageObj) throws Exception;

	public Object PUT(HttpRequest request, Object messageObj) throws Exception;

	public Object TRACE(HttpRequest request, Object messageObj) throws Exception;

	public Object GET(HttpRequest request, Object messageObj) throws Exception;

	public Object POST(HttpRequest request, Object messageObj) throws Exception;
}
