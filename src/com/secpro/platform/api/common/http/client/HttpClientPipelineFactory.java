package com.secpro.platform.api.common.http.client;

import static org.jboss.netty.channel.Channels.*;

import javax.net.ssl.SSLEngine;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.ssl.SslHandler;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;

import com.secpro.platform.api.client.Client;
import com.secpro.platform.api.common.http.securechat.SecureChatSslContextFactory;

/**
 * @author baiyanwei Jul 8, 2013
 * 
 *         The factory of HTTP client pipeline.
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	private final boolean _ssl;
	private SimpleChannelUpstreamHandler _responseListener = null;
	private int _readTimeoutSeconds = 30;
	private int _writeTimeoutSeconds = 30;

	public HttpClientPipelineFactory(boolean ssl, SimpleChannelUpstreamHandler responseListener, int readTimeoutSeconds, int writeTimeoutSeconds) {
		this._ssl = ssl;
		this._responseListener = responseListener;
		this._readTimeoutSeconds = readTimeoutSeconds;
		this._writeTimeoutSeconds = writeTimeoutSeconds;

	}

	public HttpClientPipelineFactory(boolean ssl, SimpleChannelUpstreamHandler responseListener) {
		this(ssl, responseListener, 30, 30);
	}

	public HttpClientPipelineFactory() {
		this(false, null, 30, 30);
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		// Enable Reading and writing timeOut.
		if (_readTimeoutSeconds > 0) {
			// 1 HTTP response timeOut timer.
			pipeline.addFirst(Client.READ_TIME_OUT_PIPE_LINE, new ReadTimeoutHandler(new HashedWheelTimer(), _readTimeoutSeconds));
		}
		if (_writeTimeoutSeconds > 0) {
			// 2 HTTP request timeOut timer.
			pipeline.addFirst(Client.WRITE_TIME_OUT_PIPE_LINE, new WriteTimeoutHandler(new HashedWheelTimer(), _writeTimeoutSeconds));
		}
		// Enable HTTPS if necessary.
		if (_ssl) {
			// 3 HTTPS SSL handler
			SSLEngine engine = SecureChatSslContextFactory.getClientContext().createSSLEngine();
			engine.setUseClientMode(true);
			pipeline.addLast("ssl", new SslHandler(engine));
		}
		// 4 HTTP response and request EnCode DeCode.
		pipeline.addLast("codec", new HttpClientCodec());

		// 5 automatic content compression
		pipeline.addLast("deflater", new HttpContentCompressor(1));
		// 6 automatic content decompression.
		pipeline.addLast("inflater", new HttpContentDecompressor());

		// 7 HTTP handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		// 8 HTTP response handler
		pipeline.addLast("contentHandler", new HttpResponseHandler());
		// 9 business operation handler
		if (_responseListener != null) {
			pipeline.addLast("responseListener", _responseListener);
		}
		return pipeline;
	}
}
