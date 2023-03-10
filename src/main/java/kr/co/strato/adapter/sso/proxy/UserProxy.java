package kr.co.strato.adapter.sso.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.sso.model.dto.UserDTO;
import kr.co.strato.adapter.sso.model.req.UsersReq;

@FeignClient(value="strato-portal-user", url = "${strato.portal.url}")
public interface UserProxy {

	@PostMapping("/api/v1/portal/users")
	public List<UserDTO> getUsers(
			@RequestHeader Map<String, Object> header, 
			@RequestBody UsersReq param);
}
