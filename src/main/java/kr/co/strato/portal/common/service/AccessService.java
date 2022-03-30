package kr.co.strato.portal.common.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class AccessService {
	
	@Autowired
	KeyCloakApiUtil keyCloakApiUtil;
	
	@Autowired
	TokenValidator tokenValidator;
	
	//token 요청(로그인)
	public ResponseEntity<KeycloakToken> doLogin(UserDto dto) throws Exception {
		return keyCloakApiUtil.getTokenByUser(dto);
	}
	
	//token refresh 요청
	public ResponseEntity<KeycloakToken> tokenRefresh(String refreshToken) throws Exception {
		return keyCloakApiUtil.refreshToken(refreshToken);
	}
	
	//token 유효성 검증
	public boolean tokenVerify(String accessToken) {
		boolean result = tokenValidator.validateToken(accessToken);
		return result;
	}
	
	
	//token 삭제 요청(로그아웃)
	public void doLogout(String userId) throws Exception {
		keyCloakApiUtil.logoutUser(userId);
	}

}
