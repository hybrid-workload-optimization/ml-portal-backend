package kr.co.strato.adapter.sso.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.sso.model.dto.GroupDTO;
import kr.co.strato.adapter.sso.model.dto.GroupDetailDTO;
import kr.co.strato.adapter.sso.model.req.GroupDetailReq;
import kr.co.strato.adapter.sso.model.req.GroupReq;

@FeignClient(value="strato-portal-group", url = "${strato.portal.url}")
public interface GroupProxy {

	@PostMapping("/api/v1/portal/groups")
	public List<GroupDTO> getGroups(
			@RequestHeader Map<String, Object> header, 
			@RequestBody GroupReq param);

	@PostMapping("/api/v1/portal/group/detail")
	public GroupDetailDTO getGroupDetail(
			@RequestHeader Map<String, Object> header, 
			@RequestBody GroupDetailReq param);

}

