package kr.co.strato.portal.common.controller;


import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import kr.co.strato.global.error.type.AuthErrorType;
import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.common.model.LoginDto;
import kr.co.strato.portal.common.service.AccessService;
import kr.co.strato.portal.setting.model.UserAuthorityDto;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.ResetParam;
import kr.co.strato.portal.setting.model.UserDto.ResetRequestResult;
import kr.co.strato.portal.setting.service.AuthorityService;
import kr.co.strato.portal.setting.service.UserService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/access-manage")
@Slf4j
public class AccessController {
	
	@Autowired
	AccessService accessService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	TokenValidator tokenValidator;
	
	@Autowired
	AuthorityService authorityService;
	
	//token 요청(로그인)
	@PostMapping("/login")
	public ResponseWrapper<LoginDto> doLogin(@RequestBody UserDto dto, HttpSession session, HttpServletResponse response) throws Exception {
		
		LoginDto result = new LoginDto();
		
		try {
			String userId = dto.getUserId();
			ResponseEntity<KeycloakToken> data = accessService.doLogin(dto);
			if(data.getStatusCode() == HttpStatus.OK) {
				KeycloakToken token = data.getBody();
				UserDto user = userService.getUserInfo(userId);
				result.setToken(token);
				result.setUser(user);	
				
				//유저 권한 추가
				UserAuthorityDto authority = authorityService.getUserRole(userId);
				result.setAuthority(authority);
				
				return new ResponseWrapper<>(result);
			}else {
				return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
			}	
		}catch (HttpClientErrorException e) {
			log.error(e.getMessage(), e);
			return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
		}
	}
	
	//token refresh 요청
	@PostMapping("/token-refresh")
//	public ResponseWrapper<KeycloakToken> tokenRefresh(@RequestBody Map<String, String> refreshToken) throws Exception {
	public ResponseWrapper<LoginDto> tokenRefresh(HttpServletRequest req, @CookieValue(value ="refresh_token")String refreshToken) throws Exception {
		LoginDto result = new LoginDto();
		ResponseEntity<KeycloakToken> data = accessService.tokenRefresh(refreshToken);
		result.setToken(data.getBody());
		
		String userId = tokenValidator.extractUserInfo(data.getBody().getAccessToken()).getUserId();
		UserDto user = userService.getUserInfo(userId);
		result.setUser(user);
		
		return new ResponseWrapper<>(result);
	}
	
	//token 유효성 검증
	@PostMapping("/token-verify")
	public void tokenVerify(@RequestBody Map<String, String> token) {
		boolean result = accessService.tokenVerify(token.get("access_token"));
	}
	
	
	//token 삭제 요청(로그아웃)
	@GetMapping("/logout/{userId}")
	public void doLogout(@PathVariable String userId) throws Exception {
		accessService.doLogout(userId);
	}
	
	
	@PostMapping("/users/reset/password")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> resetUserPassword(@RequestBody ResetParam param) {
		String res = userService.resetUserPassword(param);
		return new ResponseWrapper<>(res);
	}
	
	@GetMapping("/users/reset/password/user")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ResetRequestResult> resetUserInfo(@RequestParam String requestCode) {
		ResetRequestResult req = userService.getResetUserId(requestCode);
		return new ResponseWrapper<>(req);
	}
	
	@GetMapping("/users/reset/password")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ResetRequestResult> requestResetPassword(@RequestParam String email) {
		ResetRequestResult result = userService.requestResetPassword(email);
		return new ResponseWrapper<>(result);
	}
	
}
