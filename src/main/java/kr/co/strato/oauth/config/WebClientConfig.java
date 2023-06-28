package kr.co.strato.oauth.config;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {
	
	@Value("${auth.server.url}")
	private String authServerUrl;
	
	@Value("${auth.client.url}")
	private String clientUrl;
	
	@Value("${auth.client.client-id}")
	private String clientId;
	
	@Value("${auth.client.client-secret}")
	private String clientSecret;

    @Bean
    WebClient webClient() {
    	String auth = clientId + ":" + clientSecret;
    	String encodedAuth = new String(Base64.getEncoder().encode(auth.getBytes()));
    	String authorization = "Basic " + encodedAuth;
    	
    	WebClient webClient = WebClient.builder()
    	        .baseUrl(authServerUrl)
    	        .defaultHeader("Authorization", authorization)
    	        .build();
    	
    	return webClient;
    }
    
    
	@Bean
    ClientRegistrationRepository clientRegistrationRepository() {
		String[] registrationIds = {clientId + "-oidc", clientId + "-authorization-code"};
		String[] redirectUrls = {clientUrl + "/login/oauth2/code/" + clientId + "-oidc",
				clientUrl + "/authorized"};
		
		String[] scopes = {"openid", null};
		
		String tokenUri = authServerUrl + "/oauth2/token";
		String jwkSetUri = authServerUrl + "/oauth2/jwks";
		String authorizationUri = authServerUrl + "/oauth2/authorize";
		
		List<ClientRegistration> list = new ArrayList<>();
		for(int i=0; i< registrationIds.length; i++) {
			String regId = registrationIds[i];
			String redirectUrl = redirectUrls[i];
			String scope = scopes[i];
			
			ClientRegistration c = ClientRegistration.withRegistrationId(regId)
					.clientId(clientId)
					.clientSecret(clientSecret)
					.clientAuthenticationMethod(ClientAuthenticationMethod.BASIC)
					.authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
					.tokenUri(tokenUri)
					.jwkSetUri(jwkSetUri)
					.authorizationUri(authorizationUri)
			        .redirectUri(redirectUrl)
					.scope(scope)
					.build();
			list.add(c);
		}        
        return new InMemoryClientRegistrationRepository(list);
    }
    
}