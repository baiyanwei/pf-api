package com.secpro.platform.api.common.http.client;

import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.channel.ExceptionEvent;
import org.jboss.netty.channel.MessageEvent;
import org.jboss.netty.channel.SimpleChannelUpstreamHandler;

import com.secpro.platform.api.client.IClientResponseListener;

public class HttpClientHandler extends SimpleChannelUpstreamHandler {

	private IClientResponseListener handler = null;

	public HttpClientHandler(IClientResponseListener handler) {
		this.handler = handler;
	}

	@Override
	public void messageReceived(ChannelHandlerContext ctx, MessageEvent e) throws Exception {
		this.handler.fireSucceed(e.getMessage());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, ExceptionEvent e) throws Exception {
		// TODO Auto-generated method stub
		this.handler.fireError(e);
	}
}
