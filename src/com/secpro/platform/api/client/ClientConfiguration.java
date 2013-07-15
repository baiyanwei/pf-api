package com.secpro.platform.api.client;

import java.util.HashMap;


/**
 * This class is used to pass all configuration data that is required by 
 * a client network connection.
 * @author baiyanwei
 * Jul 8, 2013
 *
 */
public class ClientConfiguration {

	public String _endPointURI = "";
	public int _endPointPort = 80;
	public boolean _synchronousConnection = true;
	public String _protocolType = "HTTP";
	public int _readBufferSize = 1000000;
	public int _packetBufferSize = 10000;
	public boolean _bUseStaticThreadPool = true;
	public String _id = "";
	public String _trafficRecordId = "";
	public boolean _isPrefetch = false;
	public Object _httpRequest = null;
	public IClientResponseListener _responseListener = null;
	public HashMap<String,String> _parameterMap= null;
	public boolean _bEnableTLS = false;

	public String toString() {
		return _endPointURI + ":" + _endPointPort;
	}

}
