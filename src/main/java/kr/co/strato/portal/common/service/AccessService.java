package kr.co.strato.portal.common.service;

import org.springframework.stereotype.Service;

@Service
public class AccessService {
	
	
	//token 삭제 요청(로그아웃)
	public void doLogout(String userId) throws Exception {
		//keyCloakApiUtil.logoutUser(userId);
	}

}
