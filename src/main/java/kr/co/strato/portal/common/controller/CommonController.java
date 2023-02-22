package kr.co.strato.portal.common.controller;

import java.util.Set;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.AccessToken.Access;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.UserRole;

@RestController
public class CommonController {
	
	@Value("${keycloak.resource}")
	private String clientId;
	
	/**
	 * 로그인한 유저 정보 반환.
	 * @return
	 */	
	public UserDto getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if(auth.getPrincipal() instanceof KeycloakPrincipal) {
			KeycloakPrincipal principal = (KeycloakPrincipal)auth.getPrincipal();

	        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
	        AccessToken accessToken = session.getToken();
	        
	        UserDto user = new UserDto();
	        user.setUserId(accessToken.getPreferredUsername());
	        user.setEmail(accessToken.getEmail());
	        
	        Access access = accessToken.getResourceAccess(clientId);
	        if(access != null) {
	        	//부여된 Client Role 조회
	        	Set<String> roles = access.getRoles();
	        	if(roles != null && roles.size() > 0) {
	        		String role = roles.iterator().next();
	        		
	        		UserRole userRole = UserRole.builder().userRoleCode(role).build();
	        		user.setUserRole(userRole);
	        	}
	        }	        
	        return user;
		}
		return null;
	}
}
