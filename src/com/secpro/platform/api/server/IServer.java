package com.secpro.platform.api.server;

import com.secpro.platform.core.services.IConfiguration;
import com.secpro.platform.core.services.ILife;

/**
 * @author baiyanwei
 * Jul 11, 2013
 * Define a HTTP Server behavior.
 *
 */
public interface IServer extends ILife, IConfiguration {

	public void addHandler(IHttpRequestHandler handler) throws Exception;

	public IHttpRequestHandler getHandler(String path);

	public IHttpRequestHandler removeHandler(IHttpRequestHandler handler);
}
