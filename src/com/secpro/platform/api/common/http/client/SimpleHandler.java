package com.secpro.platform.api.common.http.client;

import com.secpro.platform.api.IAPIServer.IHandler;

public class SimpleHandler implements IHandler {

	private String id = null;
	private String name = null;
	private String description = null;

	@Override
	public void fireSucceed(final Object messageObj) throws Exception {
		new Thread() {
			public void run() {
				System.out.println(SimpleHandler.this.hashCode()+"Get response Size:>>>>" + messageObj.toString().length());
			}
		}.start();
	}

	@Override
	public void fireError(final Object messageObj) throws Exception {
		new Thread() {
			public void run() {
				System.out.println("fireError>>>>" + messageObj);
			}
		}.start();
	}

	@Override
	public void setID(String id) {
		this.id = id;
	}

	@Override
	public String getID() {
		return this.id;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public String getKeyPoint() {
		// TODO Auto-generated method stub
		return null;
	}
}
