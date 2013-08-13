package com.secpro.platform.api.common.http.client;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.Channels;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;
import org.jboss.netty.handler.codec.http.HttpChunk;
import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;
import org.jboss.netty.util.CharsetUtil;

public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	private boolean readingChunks = false;
	private final StringBuffer chucksContent = new StringBuffer();

	@SuppressWarnings("deprecation")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// 200<=response.code<300, size<20000,
		//
		if (!readingChunks) {
			HttpResponse response = (HttpResponse) e.getMessage();

			HttpResponseStatus status = response.getStatus();
			if (status.getCode() < 200 || status.getCode() >= 300) {
				Channels.fireExceptionCaught(ctx, new Exception("HTTP Response Code Exception, The Response code is " + status.getCode()));
				return;
			}
			if (response.getContentLength(0) > 20000) {
				// response.getHeaders(name)
				Channels.fireExceptionCaught(ctx, new Exception("Too Many Content on Response Body"));
				return;
			}
			// header information
			// System.out.println("STATUS: " + response.getStatus());
			// System.out.println("VERSION: " + response.getProtocolVersion());
			// System.out.println("test>>"+response.getContentLength());
			// if (!response.getHeaderNames().isEmpty()) {
			// for (String name : response.getHeaderNames()) {
			// for (String value : response.getHeaders(name)) {
			// System.out.println("HEADER: " + name + " = " + value);
			// }
			// }
			// System.out.println();
			// }
			//
			if (response.isChunked()) {
				chucksContent.setLength(0);
				readingChunks = true;
			} else {
				ChannelBuffer content = response.getContent();
				if (content.readable()) {
					Channels.fireMessageReceived(ctx, content.toString(CharsetUtil.UTF_8), e.getRemoteAddress());
				}
			}
		} else {
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				readingChunks = false;
				Channels.fireMessageReceived(ctx, chucksContent.toString(), e.getRemoteAddress());
			} else {
				if (chucksContent.length() > 20000) {
					Channels.fireExceptionCaught(ctx, new Exception("Too Many Content on Response Body"));
					return;
				}
				chucksContent.append(chunk.getContent().toString(CharsetUtil.UTF_8));
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		// TODO Auto-generated method stub
		Channels.fireExceptionCaught(ctx, e.getCause());
	}
	
}
