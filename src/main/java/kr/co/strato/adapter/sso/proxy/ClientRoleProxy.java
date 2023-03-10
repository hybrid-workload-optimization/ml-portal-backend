package kr.co.strato.adapter.sso.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;

import kr.co.strato.adapter.sso.model.dto.ClientRoleDTO;


@FeignClient(value="strato-portal-role", url = "${strato.portal.url}")
public interface ClientRoleProxy {

	@GetMapping("/api/v1/portal/clientRoles/{clientId}")
	public List<ClientRoleDTO> getClientRoles(
			@RequestHeader Map<String, Object> header, 
			@PathVariable("clientId") String clientId);
}
