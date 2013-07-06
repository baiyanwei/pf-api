package com.secpro.platform.api.services;

import java.util.ArrayList;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;

import com.secpro.platform.api.IAPIServer.IHandler;
import com.secpro.platform.api.IAPIServer.IServer;
import com.secpro.platform.core.metrics.AbstractMetricMBean;
import com.secpro.platform.core.metrics.MetricUtils;
import com.secpro.platform.core.services.IConfiguration;
import com.secpro.platform.core.services.IService;
import com.secpro.platform.core.services.PropertyLoaderService;
import com.secpro.platform.core.services.ServiceHelper;
import com.secpro.platform.log.utils.PlatformLogger;

public class APIEngineeService extends AbstractMetricMBean implements IService {
	final public String HANDLER_CONF_TITLE = "handler";
	final public String SERVER_CONF_TITLE = "com.secpro.platform.api.pf_api_server";

	private static PlatformLogger logger = PlatformLogger.getLogger(APIEngineeService.class);

	public ArrayList<IServer> apiServersList = new ArrayList<IServer>();

	@Override
	public void start() throws Exception {
		// TODO Auto-generated method stub
		startupServers();
		MetricUtils.registerMBean(this);
	}

	@Override
	public void stop() throws Exception {
		// TODO Auto-generated method stub
		shutdownServers();
	}

	/**
	 * start all API servers.
	 */
	private void startupServers() {
		PropertyLoaderService propertyLoaderService = ServiceHelper.findService(PropertyLoaderService.class);
		if (propertyLoaderService == null) {
			System.err.println("Can't find the PropertyLoaderService, Server can't start without PropertyLoaderService.");
			return;
		}
		IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor(SERVER_CONF_TITLE);
		for (IConfigurationElement configElement : config) {
			try {
				// Instance implement class .
				IServer server = (IServer) configElement.createExecutableExtension(IConfiguration.IMPLEMENT_CLASS_CONF_TITLE);
				server.setID(configElement.getAttribute(IConfiguration.ID_CONF_TITLE));
				server.setName(configElement.getAttribute(IConfiguration.NAME_CONF_TITLE));
				server.setDescription(configElement.getAttribute(IConfiguration.DESCRIPTION_CONF_TITLE));
				// loading the properties from configuration.
				propertyLoaderService.loadExtensionProperties(configElement.getChildren(IConfiguration.PROPERTY_CONF_TITLE), server);
				IConfigurationElement[] handlerConfig = configElement.getChildren(HANDLER_CONF_TITLE);
				if (handlerConfig != null && handlerConfig.length != 0) {
					for (IConfigurationElement handlerElement : handlerConfig) {
						IHandler handler = (IHandler) handlerElement.createExecutableExtension(IConfiguration.IMPLEMENT_CLASS_CONF_TITLE);
						//
						handler.setID(handlerElement.getAttribute(IConfiguration.ID_CONF_TITLE));
						handler.setName(handlerElement.getAttribute(IConfiguration.NAME_CONF_TITLE));
						handler.setDescription(handlerElement.getAttribute(IConfiguration.DESCRIPTION_CONF_TITLE));
						//
						propertyLoaderService.loadExtensionProperties(handlerElement.getChildren(IConfiguration.PROPERTY_CONF_TITLE), handler);
						server.addHandler(handler);
						logger.info("registerHandler", handler.getID(), handler.getName(), server.getName());
					}
				}
				//
				server.start();
				this.apiServersList.add(server);
				logger.info("registerServer", server.getName(),server.getDescription());
			} catch (CoreException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (Throwable t) {
				// TODO Auto-generated catch block
				t.printStackTrace();
			}
		}

	}

	/**
	 * shutdown all API servers.
	 */
	private void shutdownServers() {
		for (int i = 0; i < apiServersList.size(); i++) {
			final IServer targetServer = apiServersList.get(i);
			new Thread() {
				public void run() {
					try {
						targetServer.stop();
					} catch (Throwable t) {
						t.printStackTrace();
					}
				}
			}.start();
		}
	}
}
