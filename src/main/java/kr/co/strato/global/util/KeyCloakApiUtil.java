package kr.co.strato.global.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.config.Registry;
import org.apache.http.config.RegistryBuilder;
import org.apache.http.conn.socket.ConnectionSocketFactory;
import org.apache.http.conn.socket.PlainConnectionSocketFactory;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.BasicHttpClientConnectionManager;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.global.model.KeycloakUser;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class KeyCloakApiUtil {
	private static final Logger logger = LoggerFactory.getLogger(KeyCloakApiUtil.class);

	@Value("${service.keycloak.address}")
	private String keycloakUrl;

	@Value("${service.keycloak.client.id}")
	private String keycloakClientId;

	@Value("${service.keycloak.client.secret}")
	private String keycloakClientSecret;

	@Value("${service.keycloak.manager.id}")
	private String keycloakManagerId;

	@Value("${service.keycloak.manager.pw}")
	private String keycloakManagerPw;
	
	@Value("${service.keycloak.manager.client.id}")
	private String keycloakManagerClientId;
	
	@Value("${service.keycloak.manager.client.secret}")
	private String keycloakManagerClientSecret;
	

	// 토큰 발급 API
	private static final String URI_GET_TOKEN = "/auth/realms/sptek-cloud/protocol/openid-connect/token";
	
	// 관리자 토큰 발급 API
	private static final String UTI_GET_TOKEN_MANAGER = "/auth/realms/master/protocol/openid-connect/token";

	// 유저 정보 조회 API(모든 유저 조회)
	private static final String URI_GET_USERS_INFO = "/auth/admin/realms/sptek-cloud/users";

	// 유저 정보 조회 API(단일 유저 조회-username)
	private static final String URI_GET_USER_INFO = "/auth/admin/realms/sptek-cloud/users?username=";

	// 유저 생성(회원가입)
	private static final String URI_SET_USER = "/auth/admin/realms/sptek-cloud/users";
	
	// 유저 수정 , 삭제
	private static final String URI_UPDATE_USER = "/auth/admin/realms/sptek-cloud/users/{id}";
	
	private static final String URI_UPDATE_PASSWORD = "/auth/admin/realms/sptek-cloud/users/{id}/reset-password";
	
	// 사용자 ROLE 조회(GET) / 추가(POST) / 삭제
	private static final String URI_GET_USER_ROLE = "/auth/admin/realms/sptek-cloud/users/{id}/role-mappings/realm/";
	
	private static final String MASTER_KEY = "stratoEncryptionqwer1234!@#$";

 	// 토큰 생성 - 관리자
	@SuppressWarnings("unchecked")
	public String getTokenByManager() throws Exception {
		System.out.println("관리자 토큰 생성 >>>> ");

		String uriGetToken = keycloakUrl + UTI_GET_TOKEN_MANAGER;
		String token = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/x-www-form-urlencoded");
			logger.info("request uri_userInfo:" + uriGetToken);
			logger.info("request headers" + headers);

			Map<String, String> tokenMap = new HashMap<String, String>();
			tokenMap.put("grant_type", "client_credentials");
			tokenMap.put("client_id", keycloakManagerClientId);
			tokenMap.put("client_secret", keycloakManagerClientSecret);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqBody = mapper.convertValue(tokenMap, JsonNode.class);

			ResponseEntity<JsonNode> response = requestJsonNode(uriGetToken, HttpMethod.POST, headers, reqBody);

			logger.info("status code:" + response.getStatusCode());
			logger.info("response body:" + response.getBody());

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);

			System.out.println(res.toString());
			System.out.println(res.get("access_token"));

			token = (String) res.get("access_token");

		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
//					return false;
			} else {
				throw new Exception(e);
			}
		}

		return token;
	}
	
	// 토큰 생성 - 유저
	@SuppressWarnings("unchecked")
	public String getTokenByUser() throws Exception {

		String uriGetToken = keycloakUrl + URI_GET_TOKEN;
		String token = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/x-www-form-urlencoded");
			logger.info("request uri_userInfo:" + uriGetToken);
			logger.info("request headers" + headers);

			Map<String, String> tokenMap = new HashMap<String, String>();
			tokenMap.put("client_id", keycloakClientId);
			tokenMap.put("username", keycloakManagerId);
			tokenMap.put("password", keycloakManagerPw);
			tokenMap.put("grant_type", "password");
			tokenMap.put("client_secret", keycloakClientSecret);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqBody = mapper.convertValue(tokenMap, JsonNode.class);

			ResponseEntity<JsonNode> response = requestJsonNode(uriGetToken, HttpMethod.POST, headers, reqBody);

			logger.info("status code:" + response.getStatusCode());
			logger.info("response body:" + response.getBody());

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);

			System.out.println(res.toString());
			System.out.println(res.get("access_token"));

			token = (String) res.get("access_token");

		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
//					return false;
			} else {
				throw new Exception(e);
			}
		}

		return token;
	}

	// 토큰 재발급(refresh)
	@SuppressWarnings("unchecked")
	public String refreshToken() throws Exception {

		String uriGetToken = keycloakUrl + URI_GET_TOKEN;
		String token = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Content-Type", "application/x-www-form-urlencoded");
			logger.info("request uri_userInfo:" + uriGetToken);
			logger.info("request headers" + headers);

			Map<String, String> tokenMap = new HashMap<String, String>();
			tokenMap.put("client_id", keycloakClientId);
			tokenMap.put("username", keycloakManagerId);
			tokenMap.put("password", keycloakManagerPw);
			tokenMap.put("grant_type", "password");
			tokenMap.put("client_secret", keycloakClientSecret);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqBody = mapper.convertValue(tokenMap, JsonNode.class);

			ResponseEntity<JsonNode> response = requestJsonNode(uriGetToken, HttpMethod.POST, headers, reqBody);

			logger.info("status code:" + response.getStatusCode());
			logger.info("response body:" + response.getBody());

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);

			System.out.println(res.toString());
			System.out.println(res.get("access_token"));

			token = (String) res.get("access_token");

		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
//					return false;
			} else {
				throw new Exception(e);
			}
		}

		return token;
	}
	
	
	// 유저 정보 가져오기
	public KeycloakUser getUserInfoByUserId(String userId) throws Exception {

		String uriUserInfo = keycloakUrl + URI_GET_USER_INFO + userId;
		String token = getTokenByManager();

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + token);
			logger.info("request uri_userInfo:" + uriUserInfo);
			logger.info("request headers" + headers);

			ResponseEntity<String> response = request(uriUserInfo, HttpMethod.GET, headers, null);

			logger.info("status code:" + response.getStatusCode());
			logger.info("response body:" + response.getBody());

			ObjectMapper objectMapper = new ObjectMapper();
			KeycloakUser user = objectMapper.readValue(response.getBody(), KeycloakUser.class);

			if (user != null) {
				return user;
			} else {
				return null;
			}
		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				return null;
			} else {
				throw new Exception(e);
			}
		}
	}

	
	// 유저 생성
	public boolean createSsoUser(UserDto user) throws Exception {
		System.out.println("SSO USER CREATE");
		String uriCreateUser = keycloakUrl + URI_SET_USER;
		String ssoToken = "Bearer " + getTokenByManager();
		String pw = CryptoUtil.encryptAES256(user.getPassword(), MASTER_KEY);
		String ssoUser = "{ 'username' : '" + user.getUserId() + "'," + " 'enabled' : true," + " 'credentials' : ["
				+ "{'type' : 'password'," + " 'value': '" + pw + "'," + " 'temporary': false}" + "]}";

		System.out.println(ssoUser);
		System.out.println(uriCreateUser);
		System.out.println(ssoToken);

		org.json.JSONObject ssoUserInfo = new org.json.JSONObject(ssoUser);

		try {

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(uriCreateUser);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Authorization", ssoToken);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoUserInfo.toString(), "UTF-8");

			httpPost.setEntity(users);

			HttpResponse response = httpClient.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == 201) {
				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			System.err.println(e.toString());
		}

		return true;
	}
	
	
	//유저 정보 수정
	public void updateSsoUser(UserDto user, String token) throws Exception {
		System.out.println("SSO USER UPDATE");
		String uriUpdateUser = keycloakUrl + URI_SET_USER;
		String ssoToken = "Bearer " + getTokenByManager();
		String pw = CryptoUtil.encryptAES256(user.getPassword(), MASTER_KEY);
		String ssoUser = "{ 'username' : '" + user.getUserId() + "'," 
						+ " 'enabled' : true," 
						+ " 'credentials' : ["
							+ "{'type' : 'password'," 
							+ " 'value': '" + pw + "'," 
							+ " 'temporary': false}" + "]}";

		org.json.JSONObject ssoUserInfo = new org.json.JSONObject(ssoUser);

		try {

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(uriUpdateUser);
			httpPut.addHeader("Content-Type", "application/json");
			httpPut.addHeader("Authorization", ssoToken);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoUserInfo.toString(), "UTF-8");

			httpPut.setEntity(users);

			HttpResponse response = httpClient.execute(httpPut);

			if (response.getStatusLine().getStatusCode() == 201) {
				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			System.err.println(e.toString());
		}

	}
	
	//유저 삭제
	public void deleteSsoUser(UserDto user, String token) throws Exception {
		
		KeycloakUser kUser = new KeycloakUser();
		
		String uriDeleteUser = keycloakUrl + URI_SET_USER + "/" + kUser.getId();
		String ssoToken = "Bearer " + getTokenByManager();
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpDelete httpDelete = new HttpDelete(uriDeleteUser);
			httpDelete.addHeader("Content-Type", "application/json");
			httpDelete.addHeader("Authorization", ssoToken);
			
			HttpResponse response = httpClient.execute(httpDelete);
			
			if (response.getStatusLine().getStatusCode() == 201) {
				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	
	// 토큰
	@SuppressWarnings("unchecked")
	public boolean checkTokenValidationByManager(String token) throws Exception {

		String uriUserInfo = keycloakUrl + URI_GET_USER_INFO + keycloakManagerId;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + token);
			logger.info("request uri_userInfo:" + uriUserInfo);
			logger.info("request headers" + headers);

			ResponseEntity<String> response = request(uriUserInfo, HttpMethod.GET, headers, null);

			logger.info("status code:" + response.getStatusCode());
			logger.info("response body:" + response.getBody());

			ObjectMapper objectMapper = new ObjectMapper();
			ArrayList<KeycloakUser> users = objectMapper.readValue(response.getBody(), ArrayList.class);
			logger.info("user size:" + users.size());

			if (users != null && users.size() > 0) {
				return true;
			} else {
				return false;
			}
		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				return false;
			} else {
				throw new Exception(e);
			}
		}
	}


	private ResponseEntity<String> request(String uri, HttpMethod httpMethod, HttpHeaders httpHeaders,
			MultiValueMap<String, String> requestBody) throws Exception {
		RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
//		deprecated	
//		AbstractUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
//		restTemplate.setUriTemplateHandler(uriTemplateHandler);
		
		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(uri);
		
		restTemplate.setUriTemplateHandler(factory);
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);
		logger.info("request header:" + httpHeaders);
		logger.info("request body:" + requestBody);

		return restTemplate.exchange(uri, httpMethod, requestEntity, String.class);
	}

	
	
	
	
	public ResponseEntity<JsonNode> requestJsonNode(String uri, HttpMethod httpMethod, HttpHeaders httpHeaders,
			JsonNode requestBody) throws Exception {

		HttpEntity<JsonNode> requestEntity = null;

		try {

			RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
//			AbstractUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
//			restTemplate.setUriTemplateHandler(uriTemplateHandler);

			DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(uri);
			restTemplate.setUriTemplateHandler(factory);
			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

			requestEntity = new HttpEntity<JsonNode>(requestBody, httpHeaders);
			logger.info("[request]{} body={}", uri, (requestBody != null) ? requestBody.toString() : "");
			
			System.out.println("======== requestEntity :::");
			System.out.println(requestEntity.toString());
			
			return restTemplate.exchange(uri, httpMethod, requestEntity, JsonNode.class);

		} catch (HttpStatusCodeException reste) {
			logger.warn("[request]{}, request={}", uri, requestEntity.toString());
			logger.warn("[request]", reste);
			throw new Exception(reste);
		}

	}

	
//	@Bean(name = "keycloakHttpRequestFactory",autowire = Autowire.BY_NAME)
	@Bean(name = "keycloakHttpRequestFactory")
	private ClientHttpRequestFactory clientHttpRequestFactory() {
		HttpComponentsClientHttpRequestFactory clientHttpRequestFactory = null;
		try {
			TrustStrategy acceptingTrustStrategy = new TrustStrategy() {
				@Override
				public boolean isTrusted(X509Certificate[] x509Certificates, String s) throws CertificateException {
					return true;
				}
			};

			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory sslFactory = new SSLConnectionSocketFactory(sslContext,
					NoopHostnameVerifier.INSTANCE);
			Registry<ConnectionSocketFactory> socketFactoryRegistry = RegistryBuilder.<ConnectionSocketFactory>create()
					.register("https", sslFactory).register("http", new PlainConnectionSocketFactory()).build();

			BasicHttpClientConnectionManager connectionManager = new BasicHttpClientConnectionManager(
					socketFactoryRegistry);
			CloseableHttpClient httpClient = HttpClients.custom().setSSLSocketFactory(sslFactory)
					.setConnectionManager(connectionManager).build();

			clientHttpRequestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			clientHttpRequestFactory.setConnectTimeout(10000);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}

		return clientHttpRequestFactory;
	}
	

	private String replaceUri(String url, String key, String value) {
		String result = null;
		try {
			result = url.replace("{" + key + "}", URLEncoder.encode(value, "UTF-8")).trim();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return result;
		
	}
	
}