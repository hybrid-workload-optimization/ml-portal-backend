package kr.co.strato.portal.common.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.model.LoginDto;
import kr.co.strato.portal.common.service.AccessService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.service.UserService;

@RestController
@RequestMapping("/api/v1/access-manage")
public class AccessController {
	
	@Autowired
	AccessService accessService;
	
	@Autowired
	UserService userService;
	
	//token 요청(로그인)
	@PostMapping("/login")
	public ResponseWrapper<LoginDto> doLogin(@RequestBody UserDto dto, HttpSession session, HttpServletResponse response) throws Exception {
		
		LoginDto result = new LoginDto();
		
		System.out.println("로그인");
			
		KeycloakToken token = accessService.doLogin(dto);

		// @TODO 위 키클락 로그인 성공시 하는 것으로 변경 필요
		UserDto user = userService.getUserInfo(dto.getUserId());
		
		result.setToken(token);
		result.setUser(user);
		
		
		return new ResponseWrapper<>(result);
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
