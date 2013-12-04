package com.secpro.platform.api.common.http.server;

import javax.xml.bind.annotation.XmlElement;

import org.jboss.netty.handler.codec.http.HttpRequest;

import com.secpro.platform.api.server.IHttpRequestHandler;
import com.secpro.platform.log.utils.PlatformLogger;

public class SimpleHttpRequstHandler implements IHttpRequestHandler {
	private static PlatformLogger logger = PlatformLogger.getLogger(SimpleHttpRequstHandler.class);
	private String id = null;
	private String name = null;
	private String description = null;
	@XmlElement(name = "path", type = String.class)
	public String path = "";

	@Override
	public Object DELETE(HttpRequest request, Object messageObj) throws Exception {
		return "DELETE";
	}

	@Override
	public Object HEAD(HttpRequest request, Object messageObj) throws Exception {
		return "HEAD";
	}

	@Override
	public Object OPTIONS(HttpRequest request, Object messageObj) throws Exception {
		return "OPTIONS";
	}

	@Override
	public Object PUT(HttpRequest request, Object messageObj) throws Exception {
		return "PUT";
	}

	@Override
	public Object TRACE(HttpRequest request, Object messageObj) throws Exception {
		return "TRACE";
	}

	@Override
	public Object GET(HttpRequest request, Object messageObj) throws Exception {
		StringBuilder contentBuf = new StringBuilder();
		for(int i=0;i<1000;i++){
			contentBuf.append("GET");
		}
		
		return contentBuf.toString();
	}

	@Override
	public Object POST(HttpRequest request, Object messageObj) throws Exception {
		return "POST";
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
	public String getRequestMappingPath() {
		// TODO Auto-generated method stub
		return this.path;
	}

	public String toString() {
		return logger.MessageFormat("toString", name, path);
	}
}
