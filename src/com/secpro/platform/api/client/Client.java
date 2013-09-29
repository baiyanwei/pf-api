package com.secpro.platform.api.client;

import java.net.InetSocketAddress;

import org.jboss.netty.bootstrap.ClientBootstrap;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelFactory;
import org.jboss.netty.channel.ChannelFuture;
import org.jboss.netty.channel.ChannelFutureListener;
import org.jboss.netty.channel.ChannelHandler;
import org.jboss.netty.channel.ChannelPipelineFactory;
import org.jboss.netty.handler.timeout.ReadTimeoutHandler;
import org.jboss.netty.handler.timeout.WriteTimeoutHandler;

import com.secpro.platform.log.utils.PlatformLogger;

/**
 * Client description
 * 
 * @author baiyanwei Jul 8, 2013
 * 
 */
public abstract class Client implements IClient {
	public final static String READ_TIME_OUT_PIPE_LINE = "readTimeout";
	public final static String WRITE_TIME_OUT_PIPE_LINE = "writeTimeout";
	public final static String NETWORK_ERROR_CONNECTION_REFUSED = "Connection refused";
	//
	private static PlatformLogger theLogger = PlatformLogger.getLogger(Client.class);
	protected ClientConfiguration _clientConfiguration = null;
	protected InetSocketAddress _serverAddress = null;
	protected ChannelFactory _factory = null;
	protected ChannelPipelineFactory _pipelineFactory = null;
	protected ChannelHandler _handler = null;
	protected ClientBootstrap _bootstrap = null;
	protected ChannelFuture _future = null;
	protected Channel _channel = null;
	protected boolean _isUDPClient = false;

	@Override
	public void configure(ClientConfiguration config) {
		_clientConfiguration = config;
	}

	@Override
	public ClientConfiguration getConfiguration() {
		return _clientConfiguration;
	}

	@Override
	public String getId() {
		return _clientConfiguration._id;
	}

	@Override
	public void stop() {
		// This will close the socket. This happens asynchronously.
		if (_channel != null) {
			ChannelFuture future = _channel.close();
			future.addListener(new ChannelFutureListener() {

				@Override
				public void operationComplete(ChannelFuture future) throws Exception {
					if (future.isDone() == true) {
						// check timeout handle exist or not
						shutDownTimeoutTimer();
						// release external resources
						if (_bootstrap != null) {
							// Shut down executor threads to exit.
							_bootstrap.releaseExternalResources();

						}
						// This will stop the timeout handler
						_factory = null;
						_bootstrap = null;
					}
				}

			});

		}

		theLogger.debug("clientClosed", _clientConfiguration._endPointHost, _clientConfiguration._endPointPort);
	}

	/**
	 * shut down the timeout timer
	 * 
	 * @throws Exception
	 */
	protected void shutDownTimeoutTimer() throws Exception {
		if (_bootstrap != null && _bootstrap.getPipelineFactory() != null && _bootstrap.getPipelineFactory().getPipeline() != null) {
			if (_bootstrap.getPipelineFactory().getPipeline().get(READ_TIME_OUT_PIPE_LINE) != null) {
				ReadTimeoutHandler readTimerHandler = (ReadTimeoutHandler) _bootstrap.getPipelineFactory().getPipeline().remove(READ_TIME_OUT_PIPE_LINE);
				//stop the read timer
				readTimerHandler.releaseExternalResources();
			}
			if (_bootstrap.getPipelineFactory().getPipeline().get(WRITE_TIME_OUT_PIPE_LINE) != null) {
				WriteTimeoutHandler writeTimerHandler = (WriteTimeoutHandler) _bootstrap.getPipelineFactory().getPipeline().remove(WRITE_TIME_OUT_PIPE_LINE);
				//stop the write timer
				writeTimerHandler.releaseExternalResources();
			}
		}
	}
}
