package com.secpro.platform.api.common.http.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.json.JSONObject;

import com.secpro.platform.api.client.Client;
import com.secpro.platform.api.client.SimpleResponseListener;
import com.secpro.platform.api.common.http.HttpConstant;

/**
 * @author baiyanwei 
 * Jul 10, 2013 
 * 
 * a simple HTTP client ,And only support ASCII coding in URI.
 */
public class HttpClient extends Client {

	public HttpClient() {
	}

	@Override
	public void start() throws Exception {
		// if has a host.
		URI targetUri = new URI(this._clientConfiguration._endPointURI);
		if (targetUri.getHost() == null) {
			throw new Exception("invaild host.");
		}
		// if here does't have a scheme in URI, default is HTTP.
		String scheme = targetUri.getScheme() == null ? HttpConstant.HTTP_SCHEME : targetUri.getScheme();
		// if out of our supporting.
		if (!scheme.equalsIgnoreCase(HttpConstant.HTTP_SCHEME) && !scheme.equalsIgnoreCase(HttpConstant.HTTPS_SCHEME)) {
			throw new Exception("Only HTTP(S) is supported.");
		}

		// pick up a communication port.
		int port = targetUri.getPort();
		if (port == -1) {
			if (scheme.equalsIgnoreCase(HttpConstant.HTTP_SCHEME)) {
				port = HttpConstant.HTTP_PORT;
			} else if (scheme.equalsIgnoreCase(HttpConstant.HTTPS_SCHEME)) {
				port = HttpConstant.HTTPS_PORT;
			}
		}
		//
		boolean ssl = scheme.equalsIgnoreCase(HttpConstant.HTTPS_SCHEME);
		//
		if (this._clientConfiguration._parameterMap == null) {
			this._clientConfiguration._parameterMap = new HashMap<String, String>();
		}
		HttpRequest defaultHttpRequest = null;
		if (this._clientConfiguration._httpRequest == null) {
			defaultHttpRequest = HttpClient.buildHttpRequest(targetUri, this._clientConfiguration._parameterMap);
		} else {
			defaultHttpRequest = (HttpRequest) this._clientConfiguration._httpRequest;
		}
		//
		if (this._clientConfiguration._responseListener == null) {
			this._clientConfiguration._responseListener = new SimpleResponseListener();
		}
		_factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		// Configure the client.
		_bootstrap = new ClientBootstrap(_factory);
		_pipelineFactory = new HttpClientPipelineFactory(ssl, new HttpClientHandler(this._clientConfiguration._responseListener));
		// Set up the event pipeline factory.
		_bootstrap.setPipelineFactory(_pipelineFactory);
		// Start the connection attempt.
		_future = _bootstrap.connect(new InetSocketAddress(targetUri.getHost(), port));
		// Wait until the connection attempt succeeds or fails.
		_channel = _future.awaitUninterruptibly().getChannel();
		if (!_future.isSuccess()) {
			try {
				// check timeout handle exist or not
				shutDownTimeoutTimer();
				_bootstrap.releaseExternalResources();
			} catch (Exception e) {
				e.printStackTrace();
			}
			_bootstrap = null;
			_pipelineFactory = null;
			throw new Exception(_future.getCause());
		}
		// Send the HTTP request.
		_channel.write(defaultHttpRequest);
		// Wait for the server to close the connection.
		_channel.getCloseFuture().awaitUninterruptibly();
		//
		// check timeout handle exist or not
		shutDownTimeoutTimer();
		_bootstrap.releaseExternalResources();
	}

	/**
	 * @param targetUri
	 * @param httpParameterMap
	 * @return build a standard HTTP request.
	 */
	public static HttpRequest buildHttpRequest(URI targetUri, HashMap<String, String> httpParameterMap) {
		if (targetUri == null) {
			return null;
		}
		String path = targetUri.getPath();
		if (path == null || path.trim().equals("")) {
			path = HttpConstant.REQUEST_ROOT_PATH;
		}
		if (targetUri.getQuery() != null) {
			path = path + HttpConstant.REQUEST_QUERY_SPLITE_CHAR + targetUri.getQuery();
		}
		// Prepare the HTTP request.
		// TODO the path must be with ASCII coding.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, path);
		request.setHeader(HttpHeaders.Names.HOST, targetUri.getHost());
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
		JSONObject contentObj = new JSONObject(httpParameterMap);
		request.setContent(ChannelBuffers.copiedBuffer(contentObj.toString(), CharsetUtil.UTF_8));
		request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, request.getContent().readableBytes());
		// Set some example cookies.
		/*
		 * CookieEncoder httpCookieEncoder = new CookieEncoder(false);
		 * httpCookieEncoder.addCookie("my-cookie", "foo");
		 * httpCookieEncoder.addCookie("another-cookie", "bar");
		 * request.setHeader(HttpHeaders.Names.COOKIE,
		 * httpCookieEncoder.encode());
		 */
		return request;
	}
}
