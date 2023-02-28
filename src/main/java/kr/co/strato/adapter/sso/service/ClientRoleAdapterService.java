package kr.co.strato.adapter.sso.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.sso.model.ClientRoleDTO;
import kr.co.strato.adapter.sso.proxy.ClientRoleProxy;
import kr.co.strato.global.model.JwtToken;

@Service
public class ClientRoleAdapterService {
	public static final String AUTHORIZATION_KEY = "Authorization";

	@Autowired
	private ClientRoleProxy roleProxy;
	
	public List<ClientRoleDTO> getClientRole(String clientId) {
		List<ClientRoleDTO> response = roleProxy.getClientRoles(authorizationHeader(), clientId);
		return response;
	}
	
	/**
	 * 통합 포탈 API에 접근하기 위해 인증 정보 추가
	 * @return
	 */
	public Map<String, Object> authorizationHeader() {
		Map<String, Object> header = new HashMap<>();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();		
		if(auth.getPrincipal() instanceof JwtToken) {
			JwtToken principal = (JwtToken)auth.getPrincipal();
	        
	        String accessTokenStr  = principal.getAccessTokenStr();
	        
	        //System.out.println(accessTokenStr);
	        header.put("Content-Type", "application/json");
	        header.put(AUTHORIZATION_KEY, accessTokenStr);
		}
		return header;
	}
}
