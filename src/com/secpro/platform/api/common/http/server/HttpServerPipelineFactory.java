package com.secpro.platform.api.common.http.server;

import static org.jboss.netty.channel.Channels.*;

import org.jboss.netty.channel.ChannelPipeline;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.codec.http.HttpContentCompressor;
import org.jboss.netty.handler.codec.http.HttpContentDecompressor;
import org.jboss.netty.handler.codec.http.HttpRequestDecoder;
import org.jboss.netty.handler.codec.http.HttpResponseEncoder;

/**
 * @author Martin Bai.
 * 
 *         Jun 20, 2012
 */
public class HttpServerPipelineFactory implements ChannelPipelineFactory {
	private HttpServer httpServer = null;

	public HttpServerPipelineFactory(HttpServer httpServer) {
		this.httpServer = httpServer;
	}

	public ChannelPipeline getPipeline() throws Exception {
		// Create a default pipeline implementation.
		ChannelPipeline pipeline = pipeline();

		// 1 HTTP request decoder
		pipeline.addLast("decoder", new HttpRequestDecoder());
		// 2 HTTP response Encode
		pipeline.addLast("encoder", new HttpResponseEncoder());
		// 3 automatic content compression
		pipeline.addLast("deflater", new HttpContentCompressor(1));
		// 4 automatic content decompression.
		pipeline.addLast("inflater", new HttpContentDecompressor());
		// 5 HTTP handle HttpChunks.
		// pipeline.addLast("aggregator", new HttpChunkAggregator(1048576));
		// 6 business operation handler
		pipeline.addLast("handler", new HttpRequestHandler(this.httpServer));
		return pipeline;
	}
}
