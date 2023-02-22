package kr.co.strato.portal.common.controller;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class LoginController {
	
	@GetMapping(path = "/login")
	public void login(HttpServletRequest request, 
			HttpServletResponse response,
			@RequestParam String redirectUrl) throws ServletException, IOException {
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if(auth.getPrincipal() instanceof KeycloakPrincipal) {
			KeycloakPrincipal principal = (KeycloakPrincipal)auth.getPrincipal();

	        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
	        AccessToken accessToken = session.getToken();
			
			log.info("Login success userId: {}", accessToken.getPreferredUsername());
			response.sendRedirect(redirectUrl);
		}
		log.error("로그인 실패!");
	}
	
	
	@GetMapping(path = "/user")
	@SuppressWarnings("rawtypes")
	public AccessToken getUserInfo() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if(auth.getPrincipal() instanceof KeycloakPrincipal) {
			KeycloakPrincipal principal = (KeycloakPrincipal)auth.getPrincipal();

	        KeycloakSecurityContext session = principal.getKeycloakSecurityContext();
	        AccessToken accessToken = session.getToken();
	        
	        String accessTokenStr  = session.getTokenString();
	        String refreshToken = session.getIdTokenString();
			
	        System.out.println("accessToken: " + accessTokenStr);
	        System.out.println("refreshToken: " + refreshToken);
	        System.out.println(accessToken.getPreferredUsername());
	        return accessToken;
		}
		return null;
	}
	
	@GetMapping("/logout")
	public String logout(HttpServletRequest request, @RequestParam String redirectUrl) throws ServletException {
		log.info("Logout!!");
		request.logout();
		return "redirect:" + redirectUrl;
	}
}
