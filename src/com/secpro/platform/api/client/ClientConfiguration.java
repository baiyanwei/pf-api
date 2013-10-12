package com.secpro.platform.api.client;

import java.util.HashMap;

import javax.xml.bind.annotation.XmlElement;

/**
 * This class is used to pass all configuration data that is required by a
 * client network connection.
 * 
 * @author baiyanwei Jul 8, 2013
 * 
 */
public class ClientConfiguration {
	@XmlElement(name = "endPointHost", type = String.class, defaultValue = "localhost")
	public String _endPointHost = "";
	@XmlElement(name = "endPointPort", type = Integer.class, defaultValue = "80")
	public int _endPointPort = 80;
	@XmlElement(name = "endPointPath", type = String.class, defaultValue = "localhost")
	public String _endPointPath = "/";
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
	public HashMap<String, String> _parameterMap = null;
	// The Client content text ,like HTTP client body.
	public String _content = null;
	public boolean _bEnableTLS = false;

	public String toString() {
		return _endPointHost + ":" + _endPointPort + "/" + this._endPointPath;
	}

}
