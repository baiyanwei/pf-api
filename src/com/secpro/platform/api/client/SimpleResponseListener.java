package com.secpro.platform.api.client;

import com.secpro.platform.core.exception.PlatformException;

/**
 * @author baiyanwei
 * Jul 11, 2013
 * simple client response listener
 *
 */
public class SimpleResponseListener implements IClientResponseListener {

	private String id = "SimpleResponseListener";
	private String name = "SimpleResponseListener";
	private String description = "SimpleResponseListener";

	@Override
	public void fireSucceed(final Object messageObj) throws PlatformException {
		System.out.println(this.hashCode() + " Get response Size:>>>>" + messageObj.toString().length());
		System.out.println(this.hashCode() + " Get response Body:>>>>" + messageObj.toString());
	}

	@Override
	public void fireError(final Object messageObj) throws PlatformException {
		System.out.println("fireError>>>>" + messageObj);
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
}
