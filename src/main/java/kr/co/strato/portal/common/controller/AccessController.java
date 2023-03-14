package kr.co.strato.portal.common.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;

import kr.co.strato.global.error.type.AuthErrorType;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.model.LoginDto;
import kr.co.strato.portal.common.service.AccessService;
import kr.co.strato.portal.setting.model.UserAuthorityDto;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.ResetRequestResult;
import kr.co.strato.portal.setting.service.AuthorityService;
import kr.co.strato.portal.setting.service.UserService;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/v1/access-manage")
@Slf4j
public class AccessController extends CommonController {
	
	@Autowired
	AccessService accessService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	AuthorityService authorityService;
	
	//로그인된 유저 정보 반환.
	@PostMapping("/user-info")
	public ResponseWrapper<LoginDto> getUserInfo() throws Exception {
		LoginDto result = new LoginDto();
		
		try {			
			UserDto user = getLoginUser();
			result.setUser(user);
			
			//유저 권한 추가
			UserAuthorityDto authority = authorityService.getUserRole(user.getUserId(), user.getUserRole().getUserRoleName());
			result.setAuthority(authority);
			
			return new ResponseWrapper<>(result);
		}catch (HttpClientErrorException e) {
			log.error(e.getMessage(), e);
			return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
		}catch (Exception e) {
			log.error(e.getMessage(), e);
			return new ResponseWrapper<>(AuthErrorType.FAIL_AUTH);
		}
	}
	
	//token 삭제 요청(로그아웃)
	@GetMapping("/logout/{userId}")
	public void doLogout(@PathVariable String userId) throws Exception {
		accessService.doLogout(userId);
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
