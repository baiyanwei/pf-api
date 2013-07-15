package com.secpro.platform.api.client;

import com.secpro.platform.core.services.ILife;

/**
 * This interface is used to communicate with the Client 
 * implementations. Code will need to all several methods
 * on the Client.
 * @author baiyanwei
 * Jul 8, 2013
 *
 */
public interface IClient extends ILife {

	/**
	 * Clients and servers need to provide the id that they were given to all
	 * the processing workflow to find service they require.
	 * 
	 * @return
	 */
	public String getId();

	/**
	 * set the client running parameter or configuration
	 * 
	 * @param client
	 */
	public void configure(ClientConfiguration client);
	
	public ClientConfiguration getConfiguration();

}
