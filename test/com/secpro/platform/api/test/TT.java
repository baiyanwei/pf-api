package com.secpro.platform.api.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;

import com.secpro.platform.api.common.http.client.HttpClient;


public class TT {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	public static void main(String[] args) throws InterruptedException {
		// TODO Auto-generated method stub
		try {
			Thread.sleep(20*1000L);
			final URI uri = new URI("http://localhost:8888/?sdsf=322");
			for(int i=0;i<100;i++){
				//Thread.sleep(10);
				try {
					new Thread(){
						public void run(){
							try {
								new HttpClient(uri,null,new HashMap<String, String>()).start();
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
