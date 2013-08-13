package com.secpro.platform.api.test;

import org.junit.Test;

import com.secpro.platform.api.client.ClientConfiguration;
import com.secpro.platform.api.common.http.client.HttpClient;

public class HttpClientTest {

	@Test
	public void testTarget() {

		HttpClient httpClient = null;
		try {
			ClientConfiguration clientCfg=new ClientConfiguration();
			clientCfg._endPointHost="localhost";
			clientCfg._endPointPort=8080;
			httpClient = new HttpClient();
			httpClient.configure(clientCfg);
			httpClient.start();
		} catch (Exception e) {
			e.printStackTrace();
			try {
				httpClient.stop();
			} catch (Exception e1) {
				e1.printStackTrace();
			}
		}
	
	}

}
