package com.secpro.platform.api.client;

import com.secpro.platform.core.exception.PlatformException;
import com.secpro.platform.core.services.IConfiguration;

/**
 * @author baiyanwei
 * Jul 11, 2013
 * Define The listener behavior of IClient,
 * the listener just fire succeed and error method,
 * you should do yourself logic it them.
 */
public interface IClientResponseListener extends IConfiguration {
	
	/**
	 * fire on successful
	 * @param messageObj
	 * @throws Exception
	 */
	public void fireSucceed(Object messageObj) throws PlatformException;

	/**
	 * fire on error
	 * @param messageObj
	 * @throws Exception
	 */
	public void fireError(Object messageObj) throws PlatformException;
	
}
