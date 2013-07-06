package com.secpro.platform.api;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

import com.secpro.platform.api.services.APIEngineeService;
import com.secpro.platform.core.services.ServiceHelper;
import com.secpro.platform.log.utils.PlatformLogger;

public class Activator implements BundleActivator {
	private static PlatformLogger logger = PlatformLogger.getLogger(Activator.class);
	private static BundleContext context;

	static BundleContext getContext() {
		return context;
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext bundleContext) throws Exception {
		Activator.context = bundleContext;
		logger.info("#PL-API is started~");
		ServiceHelper.registerService(new APIEngineeService());
	}

	/*
	 * (non-Javadoc)
	 * @see org.osgi.framework.BundleActivator#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext bundleContext) throws Exception {
		Activator.context = null;
	}

}
