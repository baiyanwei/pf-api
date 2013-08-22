package com.secpro.platform.api.common.http.client;

import static org.jboss.netty.channel.Channels.*;

import java.util.concurrent.TimeUnit;


import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpClientCodec;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.util.HashedWheelTimer;
import org.jboss.netty.util.Timer;


/**
 * @author baiyanwei Jul 8, 2013
 * 
 *         The factory of HTTP client pipeline.
 */
public class HttpClientPipelineFactory implements ChannelPipelineFactory {

	//This is static because we only need one for all our pipelines.
    //I left the hard coded 5000 for now will figure that out if this fixes the always timing out issue.
	private static Timer _timeoutTimer = new HashedWheelTimer();
	private static ReadTimeoutHandler _readTimeoutHandler = new ReadTimeoutHandler(_timeoutTimer, 30000, TimeUnit.MILLISECONDS);
	private final boolean _ssl;
	private final HttpResponseHandler _responseHandler;
	
	/*
	private final HashedWheelTimer _readTimer;
	private final HashedWheelTimer _writeTimer;
	
	private int _readTimeoutSeconds = 30;
	private int _writeTimeoutSeconds = 30;
	public HttpClientPipelineFactory(boolean ssl, SimpleChannelUpstreamHandler responseListener, int readTimeoutSeconds, int writeTimeoutSeconds) {
		this._ssl = ssl;
		this._responseListener = responseListener;
		this._readTimeoutSeconds = readTimeoutSeconds;
		this._writeTimeoutSeconds = writeTimeoutSeconds;
		_readTimer=new HashedWheelTimer();
		_writeTimer=new HashedWheelTimer();

	}
	public HttpClientPipelineFactory(boolean ssl, SimpleChannelUpstreamHandler responseListener) {
		this(ssl, responseListener, 30, 30);
	}*/
	public HttpClientPipelineFactory(boolean ssl, HttpResponseHandler responseHandler) {
		this._ssl = ssl;
		this._responseHandler = responseHandler;

	}


	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();
		/*
		// Enable Reading and writing timeOut.
		if (_readTimeoutSeconds > 0) {
			// 1 HTTP response timeOut timer.
			pipeline.addFirst(Client.READ_TIME_OUT_PIPE_LINE, new ReadTimeoutHandler(_readTimer, _readTimeoutSeconds));
		}
		if (_writeTimeoutSeconds > 0) {
			// 2 HTTP request timeOut timer.
			pipeline.addFirst(Client.WRITE_TIME_OUT_PIPE_LINE, new WriteTimeoutHandler(_writeTimer, _writeTimeoutSeconds));
		}
		// Enable HTTPS if necessary.
		if (_ssl) {
			// 3 HTTPS SSL handler
			SSLEngine engine = SecureChatSslContextFactory.getClientContext().createSSLEngine();
			engine.setUseClientMode(true);
			pipeline.addLast("ssl", new SslHandler(engine));
		}*/
		// 4 HTTP response and request EnCode DeCode.
		pipeline.addLast("codec", new HttpClientCodec());

		// 5 automatic content compression
		pipeline.addLast("deflater", new HttpContentCompressor(1));
		// 6 automatic content decompression.
		pipeline.addLast("inflater", new HttpContentDecompressor());
		//
		pipeline.addLast("timeout", _readTimeoutHandler);
		// 7 HTTP handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		// 9 business operation handler
		pipeline.addLast("BOHandler", _responseHandler);
		
		return pipeline;
	}
}
