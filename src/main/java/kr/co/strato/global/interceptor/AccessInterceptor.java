package kr.co.strato.global.interceptor;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import kr.co.strato.global.model.JwtTokenModel;


public class AccessInterceptor implements HandlerInterceptor {
	
	@Value("${keycloak.resource}")
	private String clientId;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		if(auth.getPrincipal() instanceof JwtTokenModel.payload) {
			JwtTokenModel.payload principal = (JwtTokenModel.payload)auth.getPrincipal();
			
			//부여된 Client Role 조회
			List<String> roles = principal.getClientRoles(clientId);
			if(roles != null) {				
				//TODO : roles로 상세 권한 처리
				
				return true;
			}
		}
		request.getSession(false);
		response.sendError(HttpStatus.UNAUTHORIZED.value());
		return false;
	}
}
