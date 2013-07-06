package com.secpro.platform.api.common.http.client;

import static org.jboss.netty.channel.Channels.*;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.secpro.platform.api.common.http.securechat.SecureChatSslContextFactory;

/**
 * @author Martin Bai.
 * 
 *         Jun 15, 2012
 * 
 *         The factory of HTTP client pipeline.
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	private final boolean ssl;
	private SimpleChannelUpstreamHandler handler = null;
	private int readTimeoutSeconds = 30;
	private int writeTimeoutSeconds = 30;
	private HashedWheelTimer readTimer = null;
	private HashedWheelTimer writeTimer = null;

	public HttpClientPipelineFactory(boolean ssl, SimpleChannelUpstreamHandler handler, int readTimeoutSeconds, int writeTimeoutSeconds) {
		this.ssl = ssl;
		this.handler = handler;
		this.readTimeoutSeconds = readTimeoutSeconds;
		this.writeTimeoutSeconds = writeTimeoutSeconds;

	}

	public HttpClientPipelineFactory(boolean ssl, SimpleChannelUpstreamHandler handler) {
		this(ssl, handler, 30, 30);
	}

	public HttpClientPipelineFactory() {
		this(false, null, 30, 30);
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		// Enable Reading and writing timeOut.
		if (readTimeoutSeconds > 0) {
			readTimer = new HashedWheelTimer();
			pipeline.addFirst("upTimeout", new ReadTimeoutHandler(readTimer, readTimeoutSeconds));
		}
		if (writeTimeoutSeconds > 0) {
			writeTimer = new HashedWheelTimer();
			pipeline.addFirst("downTimeout", new WriteTimeoutHandler(writeTimer, writeTimeoutSeconds));
		}
		// Enable HTTPS if necessary.
		if (ssl) {
			SSLEngine engine = SecureChatSslContextFactory.getClientContext().createSSLEngine();
			engine.setUseClientMode(true);

			pipeline.addLast("ssl", new SslHandler(engine));
		}

		pipeline.addLast("codec", new HttpClientCodec());

		// Remove the following line if you don't want automatic content
		// decompression.
		pipeline.addLast("inflater", new HttpContentDecompressor());

		// Uncomment the following line if you don't want to handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		// to handle the HTTP message.
		pipeline.addLast("httpHelpHanler", new HttpResponseHandler());
		if (handler != null) {
			pipeline.addLast("handler", handler);
		}
		return pipeline;
	}

	public void shutdownTimer() {
		if (readTimer != null) {
			readTimer.stop();
			readTimer = null;
		}
		if (writeTimer != null) {
			writeTimer.stop();
			writeTimer = null;
		}
	}
}
