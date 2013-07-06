package com.secpro.platform.api.test;


import java.net.URI;

import org.junit.Test;

import com.secpro.platform.api.common.http.client.HttpClient;

public class HttpClientTest {

	@Test
	public void testTarget() {

		HttpClient httpClient = null;
		try {
			URI target = new URI("http://10.10.1.1");
			httpClient = new HttpClient(target, null, null);
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
