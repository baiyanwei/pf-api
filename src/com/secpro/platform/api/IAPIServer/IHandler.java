package com.secpro.platform.api.IAPIServer;

import com.secpro.platform.core.services.IConfiguration;

/**
 * @author Martin Bai. define a handler for doing yourself logic. Jun 8, 2012
 */
public interface IHandler extends IConfiguration {
	
	public void fireSucceed(Object messageObj) throws Exception;

	public void fireError(Object messageObj) throws Exception;
	
	public String getKeyPoint();
}
