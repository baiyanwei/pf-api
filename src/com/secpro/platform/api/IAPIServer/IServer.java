package com.secpro.platform.api.IAPIServer;

import com.secpro.platform.core.services.IConfiguration;
import com.secpro.platform.core.services.ILife;

/**
 * @author Martin Bai. define what functions as a server should have. Jun 8,
 *         2012
 */
public interface IServer extends ILife, IConfiguration {

	public void addHandler(IHandler handler) throws Exception;

	public IHandler getHandler(String path);

	public IHandler removeHandler(IHandler handler);
}
