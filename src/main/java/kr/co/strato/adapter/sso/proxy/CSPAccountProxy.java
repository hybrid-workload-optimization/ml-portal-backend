package kr.co.strato.adapter.sso.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.sso.model.dto.CSPAccountDTO;

@FeignClient(value="strato-portal-csp-account", url = "${strato.portal.url}")
public interface CSPAccountProxy {

	@PostMapping("/api/v1/portal/csp-accounts")
	public List<CSPAccountDTO> getAccounts(
			@RequestHeader Map<String, Object> header, 
			@RequestBody CSPAccountDTO.SearchAccount search);
	
}
