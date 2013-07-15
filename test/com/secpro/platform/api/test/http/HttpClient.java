package com.secpro.platform.api.test.http;

import java.net.InetSocketAddress;
import java.net.URI;
import java.util.concurrent.Executors;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.socket.nio.NioClientSocketChannelFactory;
import org.jboss.netty.handler.codec.http.CookieEncoder;
import org.jboss.netty.handler.codec.http.DefaultHttpRequest;
import org.jboss.netty.handler.codec.http.HttpHeaders;
import org.jboss.netty.handler.codec.http.HttpMethod;
import org.jboss.netty.handler.codec.http.HttpRequest;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.handler.codec.http.HttpVersion;

public class HttpClient {
	private URI targetURI = null;

	private long startTime = -1;

	private long reponseTiming = -1;

	private HttpResponseStatus responseStatus = null;

	private long httpResponseContentSize = 0;

	public HttpClient(URI targetURI) {
		this.targetURI = targetURI;
	}

	public void start() throws Exception {

		if (this.targetURI == null) {
			throw new Exception("ivalid target.");
		}
		String scheme = targetURI.getScheme() == null ? "http" : targetURI.getScheme();
		String host = targetURI.getHost() == null ? "localhost" : targetURI.getHost();
		int port = targetURI.getPort();
		if (port == -1) {
			if (scheme.equalsIgnoreCase("http")) {
				port = 80;
			} else if (scheme.equalsIgnoreCase("https")) {
				port = 443;
			}
		}

		if (!scheme.equalsIgnoreCase("http")) {
			System.err.println("Only HTTP is supported.");
			return;
		}
		String path= targetURI.getPath();
		if(path==null||path.trim().equals("")){
			path="/";
		}
		// Configure the client.
		ClientBootstrap bootstrap = new ClientBootstrap(new NioClientSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));

		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpClientPipelineFactory(this));

		// Start the connection attempt.
		ChannelFuture future = bootstrap.connect(new InetSocketAddress(host, port));

		// Wait until the connection attempt succeeds or fails.
		Channel channel = future.awaitUninterruptibly().getChannel();
		if (!future.isSuccess()) {
			future.getCause().printStackTrace();
			bootstrap.releaseExternalResources();
			return;
		}

		// Prepare the HTTP request.
		HttpRequest request = new DefaultHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "/?sdfs=ds");
		request.setHeader(HttpHeaders.Names.HOST, host);
		request.setHeader(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.CLOSE);
		request.setHeader(HttpHeaders.Names.ACCEPT_ENCODING, HttpHeaders.Values.GZIP + "," + HttpHeaders.Values.DEFLATE);
		request.setHeader(HttpHeaders.Names.CONTENT_ENCODING, "text/plain");

		// Set some example cookies.
		CookieEncoder httpCookieEncoder = new CookieEncoder(false);
		httpCookieEncoder.addCookie("my-cookie", "foo");
		httpCookieEncoder.addCookie("another-cookie", "bar");
		request.setHeader(HttpHeaders.Names.COOKIE, httpCookieEncoder.encode());

		this.startTime = System.nanoTime();
		// Send the HTTP request.
		channel.write(request);

		// Wait for the server to close the connection.
		channel.getCloseFuture().awaitUninterruptibly();

		// Shut down executor threads to exit.
		bootstrap.releaseExternalResources();
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	public long getReponseTiming() {
		return reponseTiming;
	}

	public void setReponseTiming(long reponseTiming) {
		this.reponseTiming = reponseTiming;
		setDataReady();
	}

	public HttpResponseStatus getResponseStatus() {
		return responseStatus;
	}

	public void setResponseStatus(HttpResponseStatus responseStatus) {
		this.responseStatus = responseStatus;
	}

	public long getHttpResponseContentSize() {
		return httpResponseContentSize;
	}

	public void setHttpResponseContentSize(long httpResponseContentSize) {
		this.httpResponseContentSize = httpResponseContentSize;
	}

	private void setDataReady() {
		//
		System.out.println("target:" + this.targetURI + " , response status:" + this.responseStatus + " , content size:" + this.httpResponseContentSize + " , response timing(ms):" + (this.reponseTiming / 1000000));
	}
}
