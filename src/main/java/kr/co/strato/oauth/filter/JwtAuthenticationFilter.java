package kr.co.strato.oauth.filter;

import java.io.IOException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;

import com.google.gson.Gson;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.global.auth.ClientAuthority;
import kr.co.strato.global.auth.JwtAuthentication;
import kr.co.strato.global.auth.JwtToken;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JwtAuthenticationFilter extends GenericFilterBean {
	public static final String AUTHORIZATION_KEY = "Authorization";
	
	private String publicKey;
	private String clientId;
	private List<String> apiTokens;
	
	public JwtAuthenticationFilter(String publicKey, String clientId, String[] apiTokens) {
		this.publicKey = publicKey;
		this.clientId = clientId;
		this.apiTokens = Arrays.asList(apiTokens);
	}
	
	 @Override
	 public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {	
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		
		log.debug("JwtAuthenticationFilter start. ");
		String jwtToken = getTokenStr(httpRequest);
		log.debug("token = {}",jwtToken);
		
		if (jwtToken != null) {
			
			Authentication auth = null;
			if(apiTokens.contains(jwtToken)) {
				//로그인 없는 싱크 요청인 경우
				log.debug("Auth success. Sync Request !");
				auth = getAPIUserAuthentication();
			} else if(validateToken(jwtToken)) {
				log.debug("Auth success. token validate !");
				auth = getAuthentication(jwtToken);
			}
			
			
			if(auth != null) {
				SecurityContextHolder.getContext().setAuthentication(auth);
				chain.doFilter(request, response);
				return;
			} else {
				log.error("Auth fail. Token is not valid.");
				
				//401 처리
				//httpRequest.getSession(false);
				//httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
				//httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");	
			}
		} else {
			String requestUri = httpRequest.getRequestURI();
			System.out.println(requestUri);
			log.error("Auth fail. Token is null.");
		}
		//httpRequest.getSession(false);
		//httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED); 
		//httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "The token is not valid.");	
		
		chain.doFilter(request, response);
	}
	
	/**
	 * 헤더에서 토큰값을 구해 Bearer를 빼고 리턴한다.
	 * @param request
	 * @return
	 */
	private String getTokenStr(HttpServletRequest request) {
		String headerAuth = request.getHeader(AUTHORIZATION_KEY);
		if (StringUtils.hasText(headerAuth)) {
			if(headerAuth.startsWith("Bearer ")) {
				return headerAuth.substring(7, headerAuth.length());
			}
			return headerAuth;
		}
		/*
		else {
			//자동 로그인 임시 코드
			String authServerUrl = "http://172.16.10.177:8580/auth/"; 
			String realm = "strato-platform";
			String clientId = "strato-portal";
			Map<String, Object> clientCredentials = new HashMap<>();
			clientCredentials.put("secret", "LnLcQUFRvtRl6gUwJbKsWulVPYyt4BvF");
			
			Configuration configuration = new Configuration(authServerUrl, realm, clientId, clientCredentials, null);
			
			AuthzClient authzClient = AuthzClient.create(configuration);			
			AccessTokenResponse response = authzClient.obtainAccessToken("demouser", "test1234");
			//AccessTokenResponse response = authzClient.obtainAccessToken("demouser", "test1234");
			
			String token = response.getToken();
			return token;
		}
		*/
		return null;
	}
	
	/**
	 * Jwt token으로 부터 인증 객체를 생성하여 반환한다.
	 * @param jwtToken
	 * @return
	 */
	public Authentication getAuthentication(String jwtToken) {		
		JwtToken token = tokenParser(jwtToken);
		
		List<String> roles = token.getClientRoles(clientId);
		
		List<ClientAuthority> clientAuthoritys = null;
		if(roles != null) {
			clientAuthoritys = roles.stream().map(s -> new ClientAuthority(s)).collect(Collectors.toList());
		}
		JwtAuthentication authentication = new JwtAuthentication(token, jwtToken, clientAuthoritys);
		return authentication;
	}
	
	/**
	 * 로그인 없이 모니터링을 위한 Authentication 생성
	 * @return
	 */
	public Authentication getAPIUserAuthentication() {
		Map<String, List<String>> resourceAccess = new HashMap<>();
		List<String> clientAuthoritys = new ArrayList<>();
		clientAuthoritys.add(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN);	
		resourceAccess.put(clientId, clientAuthoritys);		
		
		JwtToken.Payload payloadInfo = new JwtToken.Payload();
		payloadInfo.setPreferredUsername("APIUser");	
		payloadInfo.setResourceAccess(resourceAccess);
		
		
		JwtToken token = JwtToken.builder()
				.payload(payloadInfo)
				.build();
		
		List<ClientAuthority> clientAuthoritys1 = new ArrayList<>();
		clientAuthoritys1.add(new ClientAuthority(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN));
		
		JwtAuthentication authentication = new JwtAuthentication(token, token, clientAuthoritys1);
		return authentication;
	}
	
	
	public RSAPublicKey getParsePublicKey() throws NoSuchAlgorithmException, InvalidKeySpecException {
		byte[] decode = Base64.getDecoder().decode(publicKey);
        X509EncodedKeySpec keySpecX509 = new X509EncodedKeySpec(decode);
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        RSAPublicKey pubKey = (RSAPublicKey) keyFactory.generatePublic(keySpecX509);
        return pubKey;
	}

	public JwtToken tokenParser(String tokenStr) {
		String[] chunks = tokenStr.split("\\.");
		Base64.Decoder decoder = Base64.getUrlDecoder();
		String header = new String(decoder.decode(chunks[0]));
		String payload = new String(decoder.decode(chunks[1]));
		
		System.out.println(payload);
		
		Gson gson = new Gson();

		JwtToken.Header headerInfo = gson.fromJson(header, JwtToken.Header.class);
		JwtToken.Payload payloadInfo = gson.fromJson(payload, JwtToken.Payload.class);
		
		JwtToken token = JwtToken.builder()
				.header(headerInfo)
				.payload(payloadInfo)
				.accessTokenStr(tokenStr)
				.build();
		
		log.debug("header >>> {}", header);
		log.debug("payload >>> {}", payload);
		
		return token;
	}
	
	/**
	 * 토큰 유효성 검증
	 * @param token
	 * @return
	 */
	public boolean validateToken(String token) {	
		try {
			RSAPublicKey k = getParsePublicKey();
			Claims claims = Jwts.parserBuilder()
					.setSigningKey(k)
					.build()
					.parseClaimsJws(token)
					.getBody();
			return true;
		}catch (ExpiredJwtException e) {
			log.error(e.getMessage(), e);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}
	
}