package kr.co.strato.global.callback;

import java.io.IOException;
import java.net.HttpURLConnection;

import org.springframework.http.client.SimpleClientHttpRequestFactory;



public class CommonClientHttpRequestFactory extends SimpleClientHttpRequestFactory{
	
	
	@Override
	protected void prepareConnection(HttpURLConnection connection, String httpMethod) throws IOException {
		super.prepareConnection(connection, httpMethod);
		connection.setReadTimeout(HttpsUtils.HTTP_READ_TIMEOUT);
		connection.setConnectTimeout(HttpsUtils.HTTP_CONNECTION_TIMEOUT);
	}
}

