package com.secpro.platform.api.common.http.client;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;
import org.jboss.netty.util.CharsetUtil;
import org.json.JSONObject;

import com.secpro.platform.api.IAPIServer.IHandler;
import com.secpro.platform.api.common.http.HttpConstant;
import com.secpro.platform.core.services.ILife;

/**
 * @author Martin Bai. a simple HTTP client ,And only support ASCII coding in
 *         URI. Jun 13, 2012
 */
public class HttpClient implements ILife {

	private URI targetUri = null;
	private IHandler hander = null;
	private HashMap<String, String> dataMap = null;
	private ClientBootstrap bootstrap = null;
	private HttpClientPipelineFactory pipelineFactory = null;

	public HttpClient(URI targetUri, IHandler hander, HashMap<String, String> dataMap) {
		this.targetUri = targetUri;
		this.hander = hander;
		this.dataMap = dataMap;
	}

	@Override
	public void start() throws Exception {
		// if has a host.
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
		HttpRequest defaultHttpRequest = HttpClient.buildHttpRequest(this.targetUri, dataMap);
		if (defaultHttpRequest == null) {
			throw new Exception("Build HTTP request Exception");
		}
		//
		if (this.hander == null) {
			this.hander = new SimpleHandler();
		}
		// Configure the client.
		bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		pipelineFactory = new HttpClientPipelineFactory(ssl, new HttpClientHandler(hander));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(pipelineFactory);
		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(targetUri.getHost(), port));
		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			try {
				pipelineFactory.shutdownTimer();
				bootstrap.releaseExternalResources();
			} catch (Exception e) {
				e.printStackTrace();
			}
			bootstrap = null;
			pipelineFactory = null;
			throw new Exception(future.getCause());
		}
		// Send the HTTP request.
		channel.write(defaultHttpRequest);
		// Wait for the server to close the connection.
		channel.getCloseFuture().awaitUninterruptibly();
		//
		pipelineFactory.shutdownTimer();
		bootstrap.releaseExternalResources();
	}

	@Override
	public void stop() throws Exception {
		if (pipelineFactory != null) {
			pipelineFactory.shutdownTimer();
		}
		if (bootstrap != null) {
			// Shut down executor threads to exit.
			bootstrap.releaseExternalResources();

		}
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
