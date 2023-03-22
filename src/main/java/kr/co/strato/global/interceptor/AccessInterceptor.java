package kr.co.strato.global.interceptor;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.servlet.HandlerInterceptor;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.global.auth.JwtToken;



public class AccessInterceptor implements HandlerInterceptor {
	
	private final String STRATO_PORTAL_CLIENT_ID = "strato-portal";
	private final String STRATO_PORTAL_ROLE_SYSTEM_ADMIN = "System Admin";
	private final String STRATO_PORTAL_ROLE_COMPANY_MANAGER = "Company Manager";
	
	@Value("${auth.clientId}")
	private String clientId;

	@Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		System.out.println(auth.getPrincipal().getClass());
		if(auth.getPrincipal() instanceof JwtToken) {
			JwtToken principal = (JwtToken)auth.getPrincipal();
			
			
			//부여된 Client Role 조회
			List<String> roles = principal.getClientRoles(clientId);
			if(roles != null) {
				//TODO : roles로 상세 권한 처리
				return true;
			}
			
			//통합 포탈 권한이 Admin인 경우 접근 허용
			List<String> portalRoles = principal.getClientRoles(STRATO_PORTAL_CLIENT_ID);
			if(portalRoles != null) {
				if(portalRoles.contains(STRATO_PORTAL_ROLE_SYSTEM_ADMIN) 
						|| portalRoles.contains(STRATO_PORTAL_ROLE_COMPANY_MANAGER)) {
					
					//CoMP Admin 권한 설정
					principal.setClientRole(clientId, Arrays.asList(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN));
					return true;
				}
			}
		}
		request.getSession(false);
		response.sendError(HttpStatus.UNAUTHORIZED.value());
		return false;
	}
}
