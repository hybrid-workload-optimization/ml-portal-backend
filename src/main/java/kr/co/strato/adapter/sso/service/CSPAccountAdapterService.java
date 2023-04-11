package kr.co.strato.adapter.sso.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.sso.model.dto.CSPAccountDTO;
import kr.co.strato.adapter.sso.proxy.CSPAccountProxy;

@Service
public class CSPAccountAdapterService {
	
	public static final String AUTHORIZATION_KEY = "Authorization";
	
	@Value("${auth.syncToken}")
	private String syncToken;
	
	@Autowired
	private CSPAccountProxy cspAccountProxy;

	public List<CSPAccountDTO> getAccounts(String serviceGroupUuid, String csp) {
		CSPAccountDTO.SearchAccount search = new CSPAccountDTO.SearchAccount();
		search.setServiceGroupUuid(serviceGroupUuid);
		search.setCsp(csp);		
		return cspAccountProxy.getAccounts(authorizationHeader(), search);
	}
	
	/**
	 * 통합 포탈 API에 접근하기 위해 인증 정보 추가
	 * @return
	 */
	public Map<String, Object> authorizationHeader() {
		Map<String, Object> header = new HashMap<>();
		header.put("Content-Type", "application/json");
        header.put(AUTHORIZATION_KEY, syncToken);
		return header;
	}
}
