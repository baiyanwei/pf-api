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

import com.secpro.platform.api.client.Client;
import com.secpro.platform.core.exception.PlatformException;
import com.secpro.platform.log.utils.PlatformLogger;

public class HttpResponseHandler extends SimpleChannelUpstreamHandler {
	private static final PlatformLogger theLogger = PlatformLogger.getLogger(HttpResponseHandler.class);
	protected Client _client = null;
	private boolean readingChunks = false;
	private final StringBuffer chucksContent = new StringBuffer();

	public HttpResponseHandler(Client client) {
		_client = client;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		// 200<=response.code<300, size<20000,
		//
		if (!readingChunks) {
			HttpResponse response = (HttpResponse) e.getMessage();

			HttpResponseStatus status = response.getStatus();
			if (status.getCode() < 200 || status.getCode() >= 300) {
				doError(new PlatformException("HTTP Response Code Exception, The Response code is "+ status.getCode()));
				ctx.getChannel().close();
				return;
				// Channels.fireExceptionCaught(ctx, new
				// Exception("HTTP Response Code Exception, The Response code is "
				// + status.getCode()));
				// return;
			}
			if (response.getContentLength(0) > 20000) {
				doError(new PlatformException("Too Many Content on Response Body ,over 20000"));
				ctx.getChannel().close();
				return;
				// Channels.fireExceptionCaught(ctx, new
				// Exception("Too Many Content on Response Body"));
				// return;
			}
			if (response.isChunked()) {
				chucksContent.setLength(0);
				readingChunks = true;
			} else {
				ChannelBuffer content = response.getContent();
				if (content.readable()) {
					doJob(content.toString(CharsetUtil.UTF_8));
					// Channels.fireMessageReceived(ctx,
					// content.toString(CharsetUtil.UTF_8),
					// e.getRemoteAddress());
				}
				ctx.getChannel().close();
			}
		} else {
			HttpChunk chunk = (HttpChunk) e.getMessage();
			if (chunk.isLast()) {
				readingChunks = false;
				doJob(chucksContent.toString());
				ctx.getChannel().close();
				// Channels.fireMessageReceived(ctx, chucksContent.toString(),
				// e.getRemoteAddress());
			} else {
				if (chucksContent.length() > 20000) {
					doError(new PlatformException("Too Many Content on Response Body ,over 20000"));
					ctx.getChannel().close();
					return;
					// Channels.fireExceptionCaught(ctx, new
					// Exception("Too Many Content on Response Body"));
					// return;
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

	private void doError(PlatformException exception) {
		if (_client.getConfiguration()._responseListener != null) {
			try {
				_client.getConfiguration()._responseListener.fireError(exception);
			} catch (PlatformException e) {
				theLogger.exception(e);
			}
		}
	}

	private void doJob(String content) {
		if (_client.getConfiguration()._responseListener != null) {
			try {
				_client.getConfiguration()._responseListener.fireSucceed(content);
			} catch (PlatformException e) {
				theLogger.exception(e);
			}
		}
	}
}
