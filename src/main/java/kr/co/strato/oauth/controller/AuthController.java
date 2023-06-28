package kr.co.strato.oauth.controller;

import java.io.IOException;
import java.time.temporal.ChronoField;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.oauth.model.Oauth2Token;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

@Slf4j
@Api(tags = {"사용자 인증 관리"})
@RequestMapping("/auth")
@RestController
public class AuthController {
	
	@Value("${auth.server.url}")
	private String authServerUrl;

	@Autowired
    private WebClient webClient;
	
	@Value("${auth.client.client-id}")
	private String clientId;
	
	@Autowired
	private OAuth2AuthorizedClientService clientService;
	
	@Operation(summary = "로그인 페이지로 이동", description = "인증 서버 로그인 페이지로 이동한다.")
	@GetMapping(value = "/login")
    public void login(
    		HttpServletResponse httpServletResponse,
    		@RequestParam String redirectUrl) throws IOException {
		String url = String.format("/oauth2/authorization/%s-oidc?redirect_uri=%s", clientId, redirectUrl);
		log.info("Redirect login page: {}", url);
		httpServletResponse.sendRedirect(url);
	}
	
	@Operation(summary = "로그 아웃", description = "인증 서버에 있는 토큰 정보를 삭제한다.(클라이언트 토큰은 알아서 날려야함)")
	@GetMapping(value = "/logout")
    public void logout(
    		HttpServletResponse httpServletResponse,
    		@RequestParam String redirectUrl, 
    		@RequestParam String refreshToken)  throws IOException {
		
		if(!StringUtils.hasText(refreshToken)) {
			log.error("Refresh Token이 존재하지 않습니다.");
			return;
		}
		
		if(!StringUtils.hasText(redirectUrl)) {
			log.error("redirectUrl 이 존재하지 않습니다.");
			return;
		}
		
		String url = String.format("%s/logout?refreshToken=%s&redirectUrl=%s", authServerUrl, refreshToken, redirectUrl);
		httpServletResponse.sendRedirect(url);
	}
	
	@Operation(summary = "토큰 갱신", description = "Refresh Token을 이용하여 Access Token을 갱신한다.")
	@PostMapping(value = "/refresh_token")
    public Oauth2Token.TokenResponse refreshToken(
    		@RequestBody Oauth2Token.RefreshTokenRequest refresh)  throws IOException {
		String refreshToken = refresh.getRefresh_token();
		
		if(!StringUtils.hasText(refreshToken)) {
			log.error("Refresh Token이 존재하지 않습니다.");
			return null;
		}
        
		Map<String, String> body = new HashMap<>();
		body.put("grant_type", "refresh_token");
		body.put("refresh_token", refreshToken);
		
		
		MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
		formData.add("grant_type", "refresh_token");
		formData.add("refresh_token", refreshToken);
		
		
		Oauth2Token.TokenResponse result = this.webClient
		          .post()
		          .uri("/oauth2/token")
		          .contentType(MediaType.APPLICATION_FORM_URLENCODED)
		          .body(BodyInserters.fromFormData(formData))
		          .retrieve()
		          .bodyToMono(Oauth2Token.TokenResponse.class)
		          .block();
		
		long timestamp = System.currentTimeMillis() / 1000;
		timestamp += result.getExpires_in();		
		result.setExpires_at(timestamp);
		
		return result;
	}
	
	@Operation(summary = "유효한 토큰인지 검사한다.", description = "유효한 토큰인지 검사한다.")
	@GetMapping(value = "/validate")
    public boolean validate(HttpServletRequest httpServletRequest)  throws IOException {
		String accessToken = httpServletRequest.getHeader("Authorization");
		if(!StringUtils.hasText(accessToken)) {
			log.error("Access Token이 존재하지 않습니다.");
			return false;
		}
		
        if(accessToken.startsWith("Bearer")) {
        	accessToken = accessToken.substring(7);
        }
        
        Map<String, String> body = new HashMap<>();
		body.put("accessToken", accessToken);
        
        Boolean result = this.webClient
		          .post()
		          .uri("/auth/validate")
		          .body(Mono.just(body), Map.class)
		          .retrieve()
		          .bodyToMono(Boolean.class)
		          .block();
		return result;
	}
	
	
	@Operation(summary = "로그인된 사용자(토큰) 정보를 조회한다.", description = "로그인된 사용자(토큰) 정보를 조회한다.")
	@GetMapping(value = "/authentication")
    public Oauth2Token.TokenResponse getAuthentication()  throws IOException {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		
		OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;

		OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
				oauth2Authentication.getAuthorizedClientRegistrationId(), oauth2Authentication.getName());

		OAuth2AccessToken accessToken = client.getAccessToken();
		OAuth2RefreshToken refreshToken = client.getRefreshToken();
		
		String accessTokenStr = accessToken.getTokenValue();
		String refreshTokenStr = refreshToken.getTokenValue();
		String tokenType = accessToken.getTokenType().getValue();
		Long expiresIn = accessToken.getExpiresAt().getLong(ChronoField.INSTANT_SECONDS);
		
		
		Oauth2Token.TokenResponse tResponse = Oauth2Token.TokenResponse.builder()
				.access_token(accessTokenStr)
				.refresh_token(refreshTokenStr)
				.token_type(tokenType)
				.expires_in(expiresIn)
				.build();
		
		return tResponse;
	}
}
