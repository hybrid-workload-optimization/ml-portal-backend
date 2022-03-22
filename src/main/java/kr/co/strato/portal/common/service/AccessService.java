package kr.co.strato.portal.common.service;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.portal.setting.model.UserDto;

@Service
public class AccessService {
	
	@Autowired
	KeyCloakApiUtil keyCloakApiUtil;
	
	//token 요청(로그인)
	public KeycloakToken doLogin(UserDto dto) throws Exception {
		System.out.println("로그인");
		
		KeycloakToken token = keyCloakApiUtil.getTokenByUser(dto);
		
		return token;
	}
	
	//token refresh 요청
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
