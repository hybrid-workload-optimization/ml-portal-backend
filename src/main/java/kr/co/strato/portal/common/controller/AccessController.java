package kr.co.strato.portal.common.controller;

import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import kr.co.strato.global.error.type.AuthErrorType;
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
		
		try {
			ResponseEntity<KeycloakToken> data = accessService.doLogin(dto);
			if(data.getStatusCode() == HttpStatus.OK) {
				KeycloakToken token = data.getBody();
				UserDto user = userService.getUserInfo(dto.getUserId());
				result.setToken(token);
				result.setUser(user);	
				return new ResponseWrapper<>(result);
			}else {
				return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
			}	
		}catch (HttpClientErrorException e) {
			e.printStackTrace();
			return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
		}
	}
	
	//token refresh 요청
	@PostMapping("/token-refresh")
	public ResponseWrapper<KeycloakToken> tokenRefresh(@RequestBody Map<String, String> refreshToken) throws Exception {
		ResponseEntity<KeycloakToken> data = accessService.tokenRefresh(refreshToken.get("refresh_token"));
		return new ResponseWrapper<>(data.getBody());
	}
	
	//token 유효성 검증
	@PostMapping("/token-verify")
	public void tokenVerify(@RequestBody Map<String, String> token) {
		
		System.out.println("토큰 검증..");
		System.out.println(token.toString());
		accessService.tokenVerify(token.get("access_token"));
	}
	
	
	//token 삭제 요청(로그아웃)
	@GetMapping("/logout/{userId}")
	public void doLogout(@PathVariable String userId) throws Exception {
		accessService.doLogout(userId);
	}
}
