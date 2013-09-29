package com.secpro.platform.api.common.http.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.secpro.platform.api.server.IHttpRequestHandler;
import com.secpro.platform.api.server.IServer;
import com.secpro.platform.core.metrics.AbstractMetricMBean;
import com.secpro.platform.core.metrics.Metric;
import com.secpro.platform.core.metrics.MetricUtils;
import com.secpro.platform.log.utils.PlatformLogger;

/**
 * @author baiyanwei
 * Jul 11, 2013
 * HTTP Server
 */
public class HttpServer extends AbstractMetricMBean implements IServer {
	private static PlatformLogger logger = PlatformLogger.getLogger(HttpServer.class);
	private ServerBootstrap bootstrap = null;
	private String id = null;
	private String name = null;
	private String description = null;
	@Metric(description = "http server port")
	@XmlElement(name = "port", type = Integer.class)
	public int port = 8080;
	@Metric(description = "http server port")
	public int requestTotal = 0;

	private HashMap<String, IHttpRequestHandler> pathMap = new HashMap<String, IHttpRequestHandler>();

	@Override
	public void start() throws Exception {
		bootstrap = new ServerBootstrap(new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool()));
		// Set up the event pipeline factory.
		bootstrap.setPipelineFactory(new HttpServerPipelineFactory(this));
		// Bind and start to accept incoming connections.
		bootstrap.bind(new InetSocketAddress(port));
		MetricUtils.registerMBean(this);
	}

	@Override
	public void stop() throws Exception {
		bootstrap.releaseExternalResources();
	}

	@Override
	public void addHandler(IHttpRequestHandler handler) throws Exception {
		if (handler == null) {
			return;
		}
		this.pathMap.put(handler.getRequestMappingPath(), handler);
	}

	@Override
	public IHttpRequestHandler getHandler(String path) {
		if (path == null || path.trim().equals("")) {
			return null;
		}
		return this.pathMap.get(path.trim());
	}

	@Override
	public IHttpRequestHandler removeHandler(IHttpRequestHandler handler) {
		if (handler == null) {
			return null;
		}
		return this.pathMap.get(handler.getName());
	}

	@Metric(description = "description of the server")
	@Override
	public String getDescription() {
		return description;
	}

	@Metric(description = "server name")
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	@Metric(description = "server id")
	@Override
	public String getID() {
		return this.id;
	}

	public String toString() {
		return logger.MessageFormat("toString", this.name, this.port);
	}

	@Metric(description = "Server running status")
	public String serverStatus() {
		return "everything is fine.";
	}
}
