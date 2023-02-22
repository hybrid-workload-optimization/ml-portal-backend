package kr.co.strato.global.interceptor;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

public class AccessInterceptor implements HandlerInterceptor {
	
	@Value("${keycloak.resource}")
	private String clientId;

	@SuppressWarnings("rawtypes")
	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		
		//String requestURI = request.getRequestURI();
		//String method = request.getMethod();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth != null && auth.getPrincipal() instanceof KeycloakPrincipal) {
			KeycloakPrincipal principal = (KeycloakPrincipal)auth.getPrincipal();

	        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
	        AccessToken accessToken = session.getToken();
	        
	        //Client ID 입력
	        Access access = accessToken.getResourceAccess(clientId);
	        if(access != null) {
	        	//부여된 Client Role 조회
	        	Set<String> roles = access.getRoles();
	        	
	        	//TODO : roles로 상세 권한 처리
	        	
	        	return true;
	        }
		}
		request.getSession(false);
		response.sendError(HttpStatus.UNAUTHORIZED.value());
		return false;
	}
}
