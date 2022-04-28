package kr.co.strato.global.util;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
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
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.http.converter.FormHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.DefaultUriBuilderFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.global.error.exception.SsoConnectionException;
import kr.co.strato.global.model.KeycloakRole;
import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.model.KeycloakUser;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KeyCloakApiUtil {
//	private static final Logger logger = LoggerFactory.getLogger(KeyCloakApiUtil.class);

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
	
	@Value("${service.keycloak.temp.pw}")
	private String keycloakTempPw;
	

	// 토큰 발급 / 갱신 API / 
	private static final String URI_GET_TOKEN = "/auth/realms/Strato-Cloud/protocol/openid-connect/token";
	
	// 관리자 토큰 발급 API
	private static final String URI_GET_TOKEN_MANAGER = "/auth/realms/master/protocol/openid-connect/token";

	// 유저 정보 조회 API(모든 유저 조회)
	private static final String URI_GET_USERS_INFO = "/auth/admin/realms/Strato-Cloud/users";

	// 유저 정보 조회 API(단일 유저 조회-username)
//	private static final String URI_GET_USER_INFO = "/auth/admin/realms/Strato-Cloud/users?username=";
	private static final String URI_GET_USER_INFO = "/auth/admin/realms/Strato-Cloud/users?briefRepresentation=true&search=";

	// 유저 생성(회원가입)
	private static final String URI_SET_USER = "/auth/admin/realms/Strato-Cloud/users";

	// 유저 수정, 삭제
	private static final String URI_UPDATE_USER = "/auth/admin/realms/Strato-Cloud/users/{id}";
	
	// 유저 비밀번호 변경
	private static final String URI_UPDATE_PASSWORD = "/auth/admin/realms/Strato-Cloud/users/{id}/reset-password";
	
	// 사용자 ROLE 조회(GET) / 추가(POST) / 삭제
	private static final String URI_USER_ROLE = "/auth/admin/realms/Strato-Cloud/users/{id}/role-mappings/realm";
	
	// 전체 ROLE 조회
	private static final String URI_GET_ROLE = "/auth/admin/realms/Strato-Cloud/roles";
	
	// 유저 로그아웃(SSO 서버 내 모든 세션 제거)
	private static final String URI_LOGOUT = "/auth/admin/realms/Strato-Cloud/users/{id}/logout";
	
	// 사용자 정보 조회
		
	// ROLE 추가(POST)
	private static final String URI_SET_ROLE = "/auth/admin/realms/Strato-Cloud/roles";
	
	// ROLE 수정(PUT), 삭제(DELETE)
	private static final String URI_UPDATE_ROLE = "/auth/admin/realms/strato-cloud/roles-by-id/{id}";
	
	private static final String URI_GET_USER_ROLE = "/auth/admin/realms/Strato-Cloud/users/{id}/role-mappings/realm/";
	//
	
	// MASTER KEY 
	private static final String MASTER_KEY = "stratoEncryptionqwer1234!@#$";

 	// 토큰 생성 - 관리자
	@SuppressWarnings("unchecked")
	public String getTokenByManager() throws Exception {
//		System.out.println("관리자 토큰 생성 >>>> ");

		String uriGetToken = keycloakUrl + URI_GET_TOKEN_MANAGER;
		String token = null;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

			MultiValueMap<String, String> tokenMap = new LinkedMultiValueMap<>();
			tokenMap.add("grant_type", "client_credentials");
			tokenMap.add("client_id", keycloakManagerClientId);
			tokenMap.add("client_secret", keycloakManagerClientSecret);
			
			HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(tokenMap, headers);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqBody = mapper.convertValue(tokenMap, JsonNode.class);

			ResponseEntity<JsonNode> response = requestPostEntity(uriGetToken, req);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);
			
			KeycloakToken ktk = convertMapToToken(res);

			
			token = (String) res.get("access_token");

		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			log.error(e.getMessage(), e);
			throw new SsoConnectionException();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new SsoConnectionException();
		}

		return "Bearer " + token;
	}
	
	// 토큰 생성 - 유저
	@SuppressWarnings("unchecked")
	public ResponseEntity<KeycloakToken> getTokenByUser(UserDto dto)  {

		KeycloakToken ktk = new KeycloakToken();
		HttpStatus status = HttpStatus.UNAUTHORIZED;
		
		String uriGetToken = keycloakUrl + URI_GET_TOKEN;
		String token = null;
		String pw;
		try {
			pw = CryptoUtil.encryptAES256(dto.getUserPassword(), MASTER_KEY);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(null);
		}
//		String pw = CryptoUtil.encryptAES256(keycloakTempPw, MASTER_KEY);
		
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			
			MultiValueMap<String, String> tokenMap = new LinkedMultiValueMap<>();
			
			tokenMap.add("client_id", keycloakClientId);
			tokenMap.add("username", dto.getUserId());
			tokenMap.add("password", pw);
			tokenMap.add("grant_type", "password");
			tokenMap.add("client_secret", keycloakClientSecret);
			
			HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(tokenMap, headers);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqBody = mapper.convertValue(tokenMap, JsonNode.class);

			ResponseEntity<JsonNode> response = requestPostEntity(uriGetToken, req);
			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);

			if(res != null) {
				ktk = convertMapToToken(res);
				status = HttpStatus.OK;
			}
			
		} catch (HttpClientErrorException e) {
			log.error(e.getMessage(), e);
			status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				return new ResponseEntity<>(null, status);
			} else {
				e.printStackTrace();
				return new ResponseEntity<>(null, e.getStatusCode());
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(ktk, status);
	}

	// 토큰 재발급(refresh)
	@SuppressWarnings("unchecked")
	public ResponseEntity<KeycloakToken> refreshToken(String refreshToken) throws Exception {

		KeycloakToken ktk = new KeycloakToken();
		String uriGetToken = keycloakUrl + URI_GET_TOKEN;

		try {
			HttpHeaders headers = new HttpHeaders();

			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
			MultiValueMap<String, String> tokenMap = new LinkedMultiValueMap<>();
			tokenMap.add("client_id", keycloakClientId);
			tokenMap.add("client_secret", keycloakClientSecret);
			tokenMap.add("grant_type", "refresh_token");
			tokenMap.add("refresh_token", refreshToken);
			
			HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(tokenMap, headers);
			

			ResponseEntity<JsonNode> response = requestPostEntity(uriGetToken, req);


			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);
			
			ktk = convertMapToToken(res);


		} catch (HttpClientErrorException e) {
			log.error(e.getMessage(), e);
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				log.error(e.getMessage(), e);
				throw new SsoConnectionException();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new SsoConnectionException();
		}

		return new ResponseEntity<>(ktk, HttpStatus.OK);
	}
	
	// 토큰 재발급(refresh) - User
	@SuppressWarnings("unchecked")
	public KeycloakToken refreshTokenByUser(KeycloakToken token) throws Exception {

		String uriGetToken = keycloakUrl + URI_GET_TOKEN;
		String result = null;
		KeycloakToken ktk = null;
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
//			headers.add("Content-Type", "application/x-www-form-urlencoded");

			MultiValueMap<String, String> tokenMap = new LinkedMultiValueMap<>();
			tokenMap.add("client_id", keycloakClientId);
			tokenMap.add("client_secret", keycloakClientSecret);
			tokenMap.add("grant_type", "refresh_token");
			tokenMap.add("refresh_token", token.getRefreshToken());
			
			HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(tokenMap, headers);

			ObjectMapper mapper = new ObjectMapper();
			JsonNode reqBody = mapper.convertValue(tokenMap, JsonNode.class);

			ResponseEntity<JsonNode> response = requestPostEntity(uriGetToken, req);

			ObjectMapper objectMapper = new ObjectMapper();
			Map<String, Object> res = objectMapper.convertValue(response.getBody(), Map.class);

			ktk = convertMapToToken(res);
			

		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				log.error(e.getMessage(), e);
				throw new SsoConnectionException();
			} else {
				log.error(e.getMessage(), e);
				throw new SsoConnectionException();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception();
		}
		
		return ktk;
	}
	
	//로그아웃 
	@SuppressWarnings("unchecked")
	public String logoutUser(String userId) throws Exception {

		String uriLogout = keycloakUrl + URI_LOGOUT;
		String result = null;
		
		
		String token = getTokenByManager();
		KeycloakUser user = getUserInfoByUserId(userId);
		String URI = replaceUri(uriLogout, "id", user.getId());
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(URI);
			httpPost.addHeader("Authorization", token);

			HttpResponse response = httpClient.execute(httpPost);

//			if (response.getStatusLine().getStatusCode() == 201) {
//				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
//			} else {
//				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
//				System.out.println(response);
//			}

		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				log.error(e.getMessage(), e);
				throw new SsoConnectionException();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception();
		}
		
		return result;
	}
	
	// 유저 정보 가져오기 - 유저 ID
	public KeycloakUser getUserInfoByUserId(String userId) throws Exception {
		String uriUserInfo = keycloakUrl + URI_GET_USER_INFO + userId;
		String token = getTokenByManager();
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization",token);

			ResponseEntity<String> response = request(uriUserInfo, HttpMethod.GET, headers, null);

			ObjectMapper objectMapper = new ObjectMapper();
			KeycloakUser[] user = objectMapper.readValue(response.getBody(), KeycloakUser[].class);

			if (user.length > 0) {
				System.out.println(user[0]);
				return user[0];
			} else {
				return null;
			}
		} catch (HttpClientErrorException e) {
			HttpStatus status = e.getStatusCode();
			if (status == HttpStatus.UNAUTHORIZED || status == HttpStatus.FORBIDDEN) {
				log.error(e.getMessage(), e);
				throw new SsoConnectionException();
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception();
		}
		return null;
	}
	
	
	
	// 유저 생성
	public boolean createSsoUser(UserDto user) throws Exception {
		String uriCreateUser = keycloakUrl + URI_SET_USER;
		String ssoToken = getTokenByManager();
		// 최초 가입시에는 임시 비밀번호로 생성
//		String pw = CryptoUtil.encryptAES256(keycloakTempPw, MASTER_KEY);
		String ssoUser = "{ 'username' : '" + user.getUserId() + "',"
				+ " 'email' : '" + user.getEmail() + "',"
				+ " 'firstName' : '" + user.getUserName() + "',"
				+ " 'enabled' : true," + ""
				+ " 'credentials' : ["
						+ "{'type' : 'password'," + " 'value': '" + keycloakTempPw + "'," + " 'temporary': false}" + "]}";
		
		org.json.JSONObject ssoUserInfo = new org.json.JSONObject(ssoUser);
		boolean result = false;

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
				result = true;
			} else {
				result = false;
			}
			
			
			/** 유저 생성 시 기본적으로 proj_member 로 등록된다고 함. 
			 * 추후 유저 생성 시 추가해야 될 기본 role 이 생기면 추가필요
			 */
			
			// 유저 생성시 무조건 프로젝트 멤버로 생성
//			KeycloakRole role = getRoleProjectMember();
			// 기본 Member
//			KeycloakRole role = new KeycloakRole();
//			role.setId("d1f29139-d14e-42c5-9025-a36a02026336");
//			role.setName("proj_member");
//			role.setDescription("프로젝트 멤버");
//			role.setComposite(false);
//			role.setClientRole(false);
//			role.setContainerId("Strato-Cloud");
//			postUserRole(user, ssoUser, role);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new Exception();
		}

		return result;
	}
	
	
	//유저 정보 수정
	public boolean updateSsoUser(UserDto user, String token) throws Exception {
		
		boolean result = false;
		String userId = getUserInfoByUserId(user.getUserId()).getId();;
		
		String uriUpdateUser = keycloakUrl + URI_UPDATE_USER;
		
		String URI = replaceUri(uriUpdateUser, "id", userId);
		
		String ssoToken = getTokenByManager();
//		String pw = CryptoUtil.encryptAES256(user.getUserPassword(), MASTER_KEY);
		String ssoUser = "{ 'username' : '" + user.getUserId() + "'," 
						+ " 'enabled' : true," 
						+ " 'email' : '" + user.getEmail() + "'}";
		org.json.JSONObject ssoUserInfo = new org.json.JSONObject(ssoUser);
		try {

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(URI);
			httpPut.addHeader("Content-Type", "application/json");
			httpPut.addHeader("Authorization", ssoToken);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoUserInfo.toString(), "UTF-8");

			httpPut.setEntity(users);

			HttpResponse response = httpClient.execute(httpPut);

			if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
				result = true;
			} else {
				result = false;
			}
			
			
			
			//@TODO ROLE 수정
//			KeycloakRole role = getRoleByUserRole(user);
//			postUserRole(user, ssoUser, role);

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	// 비밀번호 수정
	public boolean updatePasswordSsoUser(String userId, String password) throws Exception {
		boolean result = false;
		
		String uriUpdateUser = keycloakUrl + URI_UPDATE_PASSWORD;
		String URI = replaceUri(uriUpdateUser, "id", userId);
		String ssoToken = getTokenByManager();
//		String pw = CryptoUtil.encryptAES256(user.getUserPassword(), MASTER_KEY);
		String ssoUser = "{ 'type' : 'password' , " 
						+	"'temporary' : false , " 
						+ "'value' : '"+ password  + "'}";
						

		org.json.JSONObject ssoUserInfo = new org.json.JSONObject(ssoUser);
		
		try {

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPut httpPut = new HttpPut(URI);
			httpPut.addHeader("Content-Type", "application/json");
			httpPut.addHeader("Authorization", ssoToken);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoUserInfo.toString(), "UTF-8");

			httpPut.setEntity(users);

			HttpResponse response = httpClient.execute(httpPut);

			if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
				result = true;
			} else {
				result = false;
			}
			
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return result;
	}
	
	// 전체 ROLE 조회
	public List<KeycloakRole> getRoleList() throws Exception {
		
		String URI = keycloakUrl + URI_GET_ROLE;
		String ssoToken = getTokenByManager();
		
		List<KeycloakRole> result = null;
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(URI);
			httpGet.addHeader("Authorization", ssoToken);

			HttpResponse response = httpClient.execute(httpGet);
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			KeycloakRole[] roles = objectMapper.readValue(EntityUtils.toString(response.getEntity()), KeycloakRole[].class);

			result = new ArrayList<>(Arrays.asList(roles));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return result;
		
	}
	
	
	// @TODO
	// USER > ROLE 조회 - User id로 조회
	public void getUserRoleInfo(UserDto user, String ssoToken) throws Exception {
		//@TODO SSO > USer-id 가져오기
		String userId = getUserInfoByUserId(user.getUserId()).getId();
		
		String uriRoleUser = keycloakUrl + URI_USER_ROLE;
		String URI = replaceUri(uriRoleUser, "id", userId);
		KeycloakRole role = null;
//		String ssoToken = "Bearer " + getTokenByManager();
		
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(URI);
			httpGet.addHeader("Authorization", ssoToken);

			HttpResponse response = httpClient.execute(httpGet);
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			System.out.println(EntityUtils.toString(response.getEntity()));
//			role = objectMapper.readValue(response.getBody(), KeycloakRole.class);
			
			if (response.getStatusLine().getStatusCode() == HttpStatus.OK.value()) {
				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	// USER > ROLE 추가
	public void postUserRole(String userId, String roleCode) throws Exception {
		KeycloakRole role = getKeycloakRole(roleCode);
		if(role != null) {
			String ssoToken = getTokenByManager();
			deleteAllUserRole(userId, ssoToken);
			postUserRole(userId, ssoToken, role);
		}
	}
	
	
	// USER > ROLE 추가
	public void postUserRole(String userId, String token, KeycloakRole role) throws Exception {
	
		//@TODO SSO > USer-id 가져오기
		String keycloakUserId = getUserInfoByUserId(userId).getId();
		
		String url = keycloakUrl + URI_USER_ROLE;
		String URI = replaceUri(url, "id", keycloakUserId);
		String ssoRole = "[{ 'id' : '" + role.getId() + "'," 
				+ " 'name' : '" + role.getName() + "',"
				+ " 'description' : '" + role.getDescription() + "',"
				+ " 'composite' : " + role.isComposite() + ","
				+ " 'clientRole' : " + role.isClientRole() + ","
				+ " 'containerId' : '" + role.getContainerId() + "'"
				+ "}]";
		
		
		org.json.JSONArray ssoRoleInfo = new org.json.JSONArray(ssoRole);
//		org.json.JSONObject ssoRoleInfo = new org.json.JSONObject(ssoRole);
		try {

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(URI);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Authorization", token);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoRoleInfo.toString(), "UTF-8");

			httpPost.setEntity(users);

			HttpResponse response = httpClient.execute(httpPost);
			
			System.out.println(response);

			if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}
	
	public List<KeycloakRole> getUserRole(String userId) {
		List<KeycloakRole> result = null;
		try {
			String keycloakUserId = getUserInfoByUserId(userId).getId();
			String url = keycloakUrl + URI_GET_USER_ROLE;
			url = replaceUri(url, "id", keycloakUserId);
			
			String URI = keycloakUrl + URI_GET_ROLE;
			String ssoToken = getTokenByManager();
			
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpGet httpGet = new HttpGet(URI);
			httpGet.addHeader("Authorization", ssoToken);

			HttpResponse response = httpClient.execute(httpGet);
			
			ObjectMapper objectMapper = new ObjectMapper();
			
			KeycloakRole[] roles = objectMapper.readValue(EntityUtils.toString(response.getEntity()), KeycloakRole[].class);

			result = new ArrayList<>(Arrays.asList(roles));

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return result;
	}
	
	public void deleteAllUserRole(String userId) {
		try {
			String ssoToken = getTokenByManager();
			deleteAllUserRole(userId, ssoToken);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	/**
	 * Keycloak 사용자의 모든 권한 삭제
	 * @param userId
	 */
	public void deleteAllUserRole(String userId, String token) {
		List<KeycloakRole> userRoles =  getUserRole(userId);
		try {
			if(userRoles != null && userRoles.size() > 0) {
				for(KeycloakRole role : userRoles) {
					deleteUserRole(userId, token, role);
				}
			}
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	// USER > ROLE 삭제
	public void deleteUserRole(String userId, String token, KeycloakRole role) throws Exception {
		System.out.println("ROLE 삭제");
		//@TODO SSO > USer-id 가져오기
		String keycloakUserId = getUserInfoByUserId(userId).getId();
		
		String url = keycloakUrl + URI_USER_ROLE;
		String URI = replaceUri(url, "id", keycloakUserId);
		String ssoRole = "[{ 'id' : '" + role.getId() + "'," 
				+ " 'name' : '" + role.getName() + "',"
				+ " 'description' : '" + role.getDescription() + "',"
				+ " 'composite' : " + role.isComposite() + ","
				+ " 'clientRole' : " + role.isClientRole() + ","
				+ " 'containerId' : '" + role.getContainerId() + "'"
				+ "}]";
		
		System.out.println("token : " + token);
		System.out.println("URI : " + URI);
		System.out.println("ssoRole : " + ssoRole.toString());
			
		org.json.JSONArray ssoRoleInfo = new org.json.JSONArray(ssoRole);
//		org.json.JSONObject ssoRoleInfo = new org.json.JSONObject(ssoRole);
		try {

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpDeleteWithBody httpDelete = new HttpDeleteWithBody(URI);
			httpDelete.addHeader("Content-Type", "application/json");
			httpDelete.addHeader("Authorization", token);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoRoleInfo.toString(), "UTF-8");

			httpDelete.setEntity(users);

			HttpResponse response = httpClient.execute(httpDelete);
			
			System.out.println(response);

			if (response.getStatusLine().getStatusCode() == HttpStatus.NO_CONTENT.value()) {
				System.out.println("response is completed : " + response.getStatusLine().getStatusCode());
			} else {
				System.out.println("response is error : " + response.getStatusLine().getStatusCode());
			}

		} catch (Exception e) {
			System.err.println(e.toString());
		}
	}

	
	//유저 삭제
	public boolean deleteSsoUser(UserDto user) throws Exception {
		
		boolean result = false;
		String userId = getUserInfoByUserId(user.getUserId()).getId();
		String uriDeleteUser = keycloakUrl + URI_UPDATE_USER;
		String URI = replaceUri(uriDeleteUser, "id", userId);
		String ssoToken = getTokenByManager();
		try {
			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpDelete httpDelete = new HttpDelete(URI);
			httpDelete.addHeader("Content-Type", "application/json");
			httpDelete.addHeader("Authorization", ssoToken);
			HttpResponse response = httpClient.execute(httpDelete);
			if (response.getStatusLine().getStatusCode() == 204) {
				result = true;
			} else {
				result = false;
			}
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return result;
	}

	// ROLE 추가
	public boolean postRole(UserRoleEntity paramEntity) {
		String uri = keycloakUrl + URI_SET_ROLE;
		
		org.json.JSONObject ssoUserInfo = new org.json.JSONObject();
		ssoUserInfo.put("name", paramEntity.getUserRoleCode());
		ssoUserInfo.put("description", paramEntity.getDescription());
		boolean result = false;

		try {			
			String ssoToken = getTokenByManager();

			HttpClient httpClient = HttpClientBuilder.create().build();
			HttpPost httpPost = new HttpPost(uri);
			httpPost.addHeader("Content-Type", "application/json");
			httpPost.addHeader("Authorization", ssoToken);

			// string 값으로 변환 할 때 char-set 설정
			StringEntity users = new StringEntity(ssoUserInfo.toString(), "UTF-8");

			httpPost.setEntity(users);

			HttpResponse response = httpClient.execute(httpPost);

			if (response.getStatusLine().getStatusCode() == 201) {
				result = true;
			} else {
				result = false;
			}

		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return result;
	}
	
	
	// ROLE 삭제
	public boolean deleteRole(String userRoleCode) {	
		String uri = keycloakUrl + URI_UPDATE_ROLE;
		boolean result = false;

		try {
			KeycloakRole role = getKeycloakRole(userRoleCode);
			
			if(role != null) {
				uri = replaceUri(uri, "id", role.getId());
				
				String ssoToken = getTokenByManager();
				HttpClient httpClient = HttpClientBuilder.create().build();
				HttpDelete httpDelete = new HttpDelete(uri);
				httpDelete.addHeader("Content-Type", "application/json");
				httpDelete.addHeader("Authorization", ssoToken);

				HttpResponse response = httpClient.execute(httpDelete);

				if (response.getStatusLine().getStatusCode() == 204) {
					result = true;
				} else {
					result = false;
				}
			} else {
				result = false;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}

		return result;
	}
	
	// ROLE 수정
	public void putRole() {
		
	}
	
	/**
	 * Role 코드에 맞는 롤 반환.
	 * @param roleCode
	 * @return
	 * @throws Exception
	 */
	private KeycloakRole getKeycloakRole(String roleCode) throws Exception {
		KeycloakRole role = null;
		List<KeycloakRole> roles = getRoleList();
		for(KeycloakRole r : roles) {
			if(roleCode.equals(r.getName())) {
				role= r;
				break;
			}
		}
		return role;
	}
	
	
	// 토큰
	@SuppressWarnings("unchecked")
	public boolean checkTokenValidationByManager(String token) throws Exception {

		String uriUserInfo = keycloakUrl + URI_GET_USER_INFO + keycloakManagerId;

		try {
			HttpHeaders headers = new HttpHeaders();
			headers.add("Authorization", "Bearer " + token);

			ResponseEntity<String> response = request(uriUserInfo, HttpMethod.GET, headers, null);

			ObjectMapper objectMapper = new ObjectMapper();
			ArrayList<KeycloakUser> users = objectMapper.readValue(response.getBody(), ArrayList.class);
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

		DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(uri);
		
		restTemplate.setUriTemplateHandler(factory);
		restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
		restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
		restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

		HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(requestBody, httpHeaders);

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
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));

			requestEntity = new HttpEntity<JsonNode>(requestBody, httpHeaders);
			
			return restTemplate.postForEntity(uri, requestEntity, JsonNode.class);
//			return restTemplate.exchange(uri, httpMethod, requestEntity, JsonNode.class);

		} catch (HttpStatusCodeException reste) {
			log.error(reste.getMessage(), reste);
			throw new Exception(reste);
		}

	}
	
	
	public ResponseEntity<JsonNode> requestPostEntity(String uri, HttpEntity<MultiValueMap<String, String>> requestEntity) throws HttpClientErrorException, Exception {

//		HttpEntity<JsonNode> requestEntity = null;

		try {

			RestTemplate restTemplate = new RestTemplate(clientHttpRequestFactory());
//			AbstractUriTemplateHandler uriTemplateHandler = new DefaultUriTemplateHandler();
//			restTemplate.setUriTemplateHandler(uriTemplateHandler);

			DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory(uri);
			restTemplate.setUriTemplateHandler(factory);
			restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());
			restTemplate.getMessageConverters().add(new FormHttpMessageConverter());
			restTemplate.getMessageConverters().add(new StringHttpMessageConverter(Charset.forName("UTF-8")));
			
			return restTemplate.postForEntity(uri, requestEntity, JsonNode.class);
//			return restTemplate.exchange(uri, httpMethod, requestEntity, JsonNode.class);

		}catch (HttpClientErrorException e) {
//			return new ResponseEntity<>(null, e.getStatusCode());
			throw new HttpClientErrorException(e.getStatusCode());
		}catch (HttpStatusCodeException reste) {
			throw new Exception(reste);
		}

	}

	
//	@Bean(name = "keycloakHttpRequestFactory",autowire = Autowire.BY_NAME)
	@Bean(name = "keycloakHttpRequestFactory")
	private ClientHttpRequestFactory clientHttpRequestFactory(){
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
			log.error(e.getMessage(), e);
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
	
	private KeycloakToken convertMapToToken(Map<String, Object> tokenMap) {
		KeycloakToken token = new KeycloakToken();
		token.setAccessToken((String)tokenMap.get("access_token"));
		token.setExpiresIn((Integer)tokenMap.get("expires_in"));
		token.setNotBeforePolicy((Integer)tokenMap.get("not-before-policy"));
		token.setRefreshExpiresIn((Integer)tokenMap.get("refresh_expires_in"));
		token.setRefreshToken((String) tokenMap.get("refresh_token"));
		token.setScope((String) tokenMap.get("scope"));
		token.setSessionState((String) tokenMap.get("session_state"));
		
		
		return token;
	}
	
	
	/*
	public KeycloakRole getRoleByUserRole(UserDto user) throws Exception {
		KeycloakRole result = null;
		String userRole = user.getUserRole().getUserRoleCode();
		List<KeycloakRole> roles = getRoleList();
		if("PROJECT ADMIN".equals(userRole)) {
			result = roles.stream()
					.filter(r -> r.getName().equals("proj_admin"))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException());
		}else{
			result = roles.stream()
					.filter(r -> r.getName().equals("proj_member"))
					.findFirst()
					.orElseThrow(() -> new IllegalArgumentException());
		}
		return result;
	}
	*/
	
		
}