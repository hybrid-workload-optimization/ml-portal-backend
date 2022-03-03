package kr.co.strato.global.util;

import java.net.URI;

import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;

public class HttpDeleteWithBody extends HttpEntityEnclosingRequestBase {

	public static final String METHOD_NAME = "DELETE";

	public HttpDeleteWithBody() {
		super();
	}

	public HttpDeleteWithBody(URI uri) {
		super();
		this.setURI(uri);
	}

	public HttpDeleteWithBody(String uri) {
		super();
		this.setURI(URI.create(uri));
	}

	public String getMethod() {
		return METHOD_NAME;
	}

}
