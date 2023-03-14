package kr.co.strato.portal.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.auth.JwtToken;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.UserRole;

@RestController
public class CommonController {
	
	@Value("${auth.clientId}")
	private String clientId;
	
	/**
	 * 로그인한 유저 정보 반환.
	 * @return
	 */	
	public UserDto getLoginUser() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if(auth.getPrincipal() instanceof JwtToken) {
			JwtToken principal = (JwtToken)auth.getPrincipal();
			
	        UserDto user = new UserDto();
	        user.setUserId(principal.getPayload().getPreferredUsername());
	        user.setEmail(principal.getPayload().getEmail());
	        
	        List<String> roles = principal.getClientRoles(clientId);
	        if(roles != null && roles.size() > 0) {
        		String role = roles.iterator().next();
        		
        		UserRole userRole = UserRole.builder().userRoleCode(role).build();
        		user.setUserRole(userRole);
        	}       
	        return user;
		}
		return null;
	}
}
