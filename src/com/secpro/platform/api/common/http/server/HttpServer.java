package com.secpro.platform.api.common.http.server;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.concurrent.Executors;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.netty.bootstrap.ServerBootstrap;
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory;

import com.secpro.platform.api.IAPIServer.IHandler;
import com.secpro.platform.api.IAPIServer.IServer;
import com.secpro.platform.core.metrics.AbstractMetricMBean;
import com.secpro.platform.core.metrics.Metric;
import com.secpro.platform.core.metrics.MetricUtils;
import com.secpro.platform.log.utils.PlatformLogger;

/**
 * @author Martin Bai. HTTP SERVER Jun 20, 2012
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

	private HashMap<String, IHandler> pathMap = new HashMap<String, IHandler>();

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
	public void addHandler(IHandler handler) throws Exception {
		// TODO Auto-generated method stub
		if (handler == null) {
			return;
		}
		this.pathMap.put(handler.getKeyPoint(), handler);
	}

	@Override
	public IHandler getHandler(String path) {
		if (path == null || path.trim().equals("")) {
			return null;
		}
		return this.pathMap.get(path.trim());
	}

	@Override
	public IHandler removeHandler(IHandler handler) {
		if (handler == null) {
			return null;
		}
		return this.pathMap.get(handler.getName());
	}
	@Metric(description = "description of the server")
	@Override
	public String getDescription() {
		// TODO Auto-generated method stub
		return description;
	}
	@Metric(description = "server name")
	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public void setName(String name) {
		// TODO Auto-generated method stub
		this.name = name;
	}

	@Override
	public void setDescription(String description) {
		// TODO Auto-generated method stub
		this.description = description;
	}

	@Override
	public void setID(String id) {
		// TODO Auto-generated method stub
		this.id = id;
	}
	@Metric(description = "server id")
	@Override
	public String getID() {
		// TODO Auto-generated method stub
		return this.id;
	}
	public String toString(){
		return logger.MessageFormat("toString",this.name,this.port);
	}
	@Metric(description = "Server running status")
	public String serverStatus() {
		return "everything is fine.";
	}
}
