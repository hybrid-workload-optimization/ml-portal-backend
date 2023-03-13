package kr.co.strato.portal.common.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.global.validation.TokenValidator;

@Service
public class AccessService {
	
	@Autowired
	TokenValidator tokenValidator;
	
	//token 유효성 검증
	public boolean tokenVerify(String accessToken) {
		boolean result = tokenValidator.validateToken(accessToken);
		return result;
	}
	
	
	//token 삭제 요청(로그아웃)
	public void doLogout(String userId) throws Exception {
		//keyCloakApiUtil.logoutUser(userId);
	}

}
