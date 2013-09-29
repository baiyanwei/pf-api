package com.secpro.platform.api.common.http.client;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.Iterator;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpVersion;

import com.secpro.platform.api.client.Client;
import com.secpro.platform.api.client.SimpleResponseListener;
import com.secpro.platform.api.common.http.HttpConstant;
import com.secpro.platform.core.exception.PlatformException;
import com.secpro.platform.core.utils.Constants;

/**
 * @author baiyanwei Jul 10, 2013
 * 
 *         a simple HTTP client ,And only support ASCII coding in URI.
 */
public class HttpClient extends Client {

	public HttpClient() {
	}

	@Override
	public void start() throws Exception {
		// identify the client configuration object
		if (this._clientConfiguration == null) {
			throw new PlatformException("Client clientConfiguration is invalid");
		}
		// if has a host.
		if (this._clientConfiguration._endPointHost == null) {
			throw new Exception("invalid _endPointHost.");
		}
		// if here does't have a scheme in URI, default is HTTP.
		String scheme = this._clientConfiguration._protocolType;
		// if out of our supporting.
		if (!scheme.equalsIgnoreCase(HttpConstant.HTTP_SCHEME) && !scheme.equalsIgnoreCase(HttpConstant.HTTPS_SCHEME)) {
			throw new Exception("Only HTTP(S) is supported.");
		}

		// pick up a communication port.
		int port = this._clientConfiguration._endPointPort;
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
			defaultHttpRequest = buildHttpRequest();
		} else {
			defaultHttpRequest = (HttpRequest) this._clientConfiguration._httpRequest;
		}
		// fill the head parameter into HTTP request.
		fillHttpRequestHeadParameter(defaultHttpRequest, this._clientConfiguration._parameterMap);
		// fill the content
		fillHttpRequestContent(defaultHttpRequest, this._clientConfiguration._content);
		//
		if (this._clientConfiguration._responseListener == null) {
			this._clientConfiguration._responseListener = new SimpleResponseListener();
		}
		_factory = new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool());
		// Configure the client.
		_bootstrap = new ClientBootstrap(_factory);
		_pipelineFactory = new HttpClientPipelineFactory(ssl, new HttpResponseHandler(this));
		// Set up the event pipeline factory.
		_bootstrap.setPipelineFactory(_pipelineFactory);
		// Start the connection attempt.
		_future = _bootstrap.connect(new InetSocketAddress(this._clientConfiguration._endPointHost, port));
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
			//System.out.println(_future.getCause().getMessage());
			//System.out.println(_future.getCause().getClass());
			throw new Exception(_future.getCause().getMessage());
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
	 * fill the parameter into request header.
	 * 
	 * @param request
	 * @param httpParameterMap
	 */
	private void fillHttpRequestHeadParameter(HttpRequest request, HashMap<String, String> httpParameterMap) {
		for (Iterator<String> keyIter = httpParameterMap.keySet().iterator(); keyIter.hasNext();) {
			String keyName = keyIter.next();
			request.setHeader(keyName, httpParameterMap.get(keyName));
		}
	}

	/**
	 * fill the content into request.
	 * 
	 * @param request
	 * @param contentStr
	 */
	private void fillHttpRequestContent(HttpRequest request, String contentStr) {
		if (contentStr != null) {
			request.setContent(ChannelBuffers.copiedBuffer(this._clientConfiguration._content, Constants.DEFAULT_CHARSET));
			request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, request.getContent().readableBytes());
		} else {
			request.setHeader(HttpHeaders.Names.CONTENT_LENGTH, 0);
		}
	}

	/**
	 * @param targetUri
	 * @param httpParameterMap
	 * @return build a standard HTTP request.
	 */
	private HttpRequest buildHttpRequest() {
		// Prepare the HTTP request.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, this._clientConfiguration._endPointPath);
		request.setHeader(HttpHeaders.Names.HOST, this._clientConfiguration._endPointHost);
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP);
		return request;
	}
}
