package kr.co.strato.oauth.config.oauth;

import static kr.co.strato.oauth.config.oauth.HttpCookieOAuth2AuthorizationRequestRepository.REDIRECT_URI_PARAM_COOKIE_NAME;

import java.io.IOException;
import java.time.temporal.ChronoField;
import java.util.Base64;
import java.util.Optional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2RefreshToken;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.oauth.config.oauth.util.CookieUtils;
import kr.co.strato.oauth.model.Oauth2Token;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository;

    private OAuth2AuthorizedClientService clientService;
    
    public OAuth2AuthenticationSuccessHandler(
    		HttpCookieOAuth2AuthorizationRequestRepository httpCookieOAuth2AuthorizationRequestRepository,
    		OAuth2AuthorizedClientService clientService) {
        this.httpCookieOAuth2AuthorizationRequestRepository = httpCookieOAuth2AuthorizationRequestRepository;
        this.clientService = clientService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        clearAuthenticationAttributes(request, response);
        
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

	protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) {
		Optional<String> redirectUri = CookieUtils.getCookie(request, REDIRECT_URI_PARAM_COOKIE_NAME)
				.map(Cookie::getValue);		
		
		String targetUrl = redirectUri.orElse(getDefaultTargetUrl());
		
		if (authentication instanceof OAuth2AuthenticationToken) {
			OAuth2AuthenticationToken oauth2Authentication = (OAuth2AuthenticationToken) authentication;

			OAuth2AuthorizedClient client = clientService.loadAuthorizedClient(
					oauth2Authentication.getAuthorizedClientRegistrationId(), oauth2Authentication.getName());

			OAuth2AccessToken accessToken = client.getAccessToken();
			OAuth2RefreshToken refreshToken = client.getRefreshToken();
			
			String accessTokenStr = accessToken.getTokenValue();
			String refreshTokenStr = refreshToken.getTokenValue();
			String tokenType = accessToken.getTokenType().getValue();
			Long expiresAt = accessToken.getExpiresAt().getLong(ChronoField.INSTANT_SECONDS);
			//Long refreshExpiresAt = refreshToken.getExpiresAt().getLong(ChronoField.INSTANT_SECONDS);
			
			//임시로 1시간 지정 (refreshToken.getExpiresAt() null이 넘어옴..)
			Long refreshExpiresAt = refreshToken.getIssuedAt().getLong(ChronoField.INSTANT_SECONDS) + 3600;
			
			Oauth2Token.TokenResponse tResponse = Oauth2Token.TokenResponse.builder()
					.access_token(accessTokenStr)
					.refresh_token(refreshTokenStr)
					.token_type(tokenType)
					.expires_at(expiresAt)
					.refresh_expires_at(refreshExpiresAt)
					.build();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String tokenJson = gson.toJson(tResponse);
			
			log.info("토큰 발급 완료!");
			log.info(tokenJson);
			
			String encodedToken = Base64.getEncoder().encodeToString(tokenJson.getBytes());
			
			return  UriComponentsBuilder.fromUriString(targetUrl)
					.queryParam("token", encodedToken).build()
					.toUriString();
		}

		return null;
	}

    protected void clearAuthenticationAttributes(HttpServletRequest request, HttpServletResponse response) {
        super.clearAuthenticationAttributes(request);
        httpCookieOAuth2AuthorizationRequestRepository.removeAuthorizationRequestCookies(request, response);
    }

}
