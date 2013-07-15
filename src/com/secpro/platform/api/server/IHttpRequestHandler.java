package com.secpro.platform.api.server;

import org.jboss.netty.handler.codec.http.HttpRequest;

import com.secpro.platform.api.client.IClientResponseListener;

/**
 * @author baiyanwei HTTP request handler
 */
public interface IHttpRequestHandler extends IClientResponseListener {
	//
	public Object DELETE(HttpRequest request, Object messageObj) throws Exception;

	public Object HEAD(HttpRequest request, Object messageObj) throws Exception;

	public Object OPTIONS(HttpRequest request, Object messageObj) throws Exception;

	public Object PUT(HttpRequest request, Object messageObj) throws Exception;

	public Object TRACE(HttpRequest request, Object messageObj) throws Exception;

	public Object GET(HttpRequest request, Object messageObj) throws Exception;

	public Object POST(HttpRequest request, Object messageObj) throws Exception;
	
	/**
	 * Get mapping path of handler 
	 * @return
	 */
	public String getRequestMappingPath();
}
