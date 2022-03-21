package kr.co.strato.portal.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.global.util.KeyCloakApiUtil;

@Service
public class AuthService {
	
	@Autowired
	KeyCloakApiUtil keyCloakApiUtil;
	
	//token 요청(로그인)
	public void doLogin() {
		System.out.println("로그인");
//		keyCloakApiUtil.getTokenByUser(null);
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
