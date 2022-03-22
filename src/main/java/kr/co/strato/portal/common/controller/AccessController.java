package kr.co.strato.portal.common.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.portal.common.service.AccessService;
import kr.co.strato.portal.setting.model.UserDto;

@RestController
@RequestMapping("/api/v1/access-manage")
public class AccessController {
	
	@Autowired
	AccessService accessService;
	
	//token 요청(로그인)
	@PostMapping("/login")
	public ResponseEntity<?> doLogin(@RequestBody UserDto dto, HttpSession session, HttpServletResponse response) throws Exception {
		System.out.println("로그인");
		
		KeycloakToken token = accessService.doLogin(dto);
		
		//Token 값 쿠키에 저장
		Cookie tokenCookie = new Cookie("token", token.toString());
		tokenCookie.setHttpOnly(true);
		tokenCookie.setSecure(true);
		
		response.addCookie(tokenCookie);
		
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	//token refresh 요청
	@GetMapping("/token-refresh")
	public void tokenRefresh() {
		System.out.println("token refresh..");
	}
	
	//token 유효성 검증
	public void tokenVerify() {
		System.out.println("토큰 검증..");
	}
	
	
	//token 삭제 요청(로그아웃)
	public void doLogout() {
		System.out.println("do Logout..");
	}
}
