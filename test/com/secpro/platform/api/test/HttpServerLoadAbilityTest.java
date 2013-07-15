package com.secpro.platform.api.test;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.secpro.platform.api.test.http.HttpClient;
import com.secpro.platform.log.utils.PlatformLogger;

public class HttpServerLoadAbilityTest  {
	PlatformLogger logger = null;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		logger = PlatformLogger.getLogger(HttpServerLoadAbilityTest.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testLoadAbilityUnder100(){
		try {
			final URI uri = new URI("http://localhost:8888/?sdsf=322");
			for(int i=0;i<10000;i++){
				try {
					new Thread(){
						public void run(){
							try {
								new HttpClient(uri).start();
							} catch (Exception e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
	}
}
