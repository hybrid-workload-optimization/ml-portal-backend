package kr.co.strato.adapter.sso.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.sso.common.DtoMapper;
import kr.co.strato.adapter.sso.model.dto.ClientRoleDTO;
import kr.co.strato.adapter.sso.model.dto.GroupDTO;
import kr.co.strato.adapter.sso.model.dto.GroupDetailDTO;
import kr.co.strato.adapter.sso.model.dto.UserDTO;
import kr.co.strato.adapter.sso.model.req.GroupDetailReq;
import kr.co.strato.adapter.sso.model.req.GroupReq;
import kr.co.strato.adapter.sso.model.req.UsersReq;
import kr.co.strato.adapter.sso.proxy.ClientRoleProxy;
import kr.co.strato.adapter.sso.proxy.GroupProxy;
import kr.co.strato.adapter.sso.proxy.UserProxy;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

/**
 * 통합포탈 데이터 조회
 *  Service Group / User / Client Role ...
 */
@Slf4j
@Service
public class PortalAdapterService {

	public static final String AUTHORIZATION_KEY = "Authorization";

	@Autowired
	private UserProxy userProxy;

	@Autowired
	private ClientRoleProxy roleProxy;
	
	@Autowired
	private GroupProxy groupProxy;
	
	@Value("${auth.client.client-id}")
	private String clientId;
	
	@Value("${auth.syncToken}")
	private String syncToken;
	
	/**
	 * 사용자 리스트 조회
	 * @return
	 */
	public List<UserDto> getUsers() {		
		log.info("Search user list");		
		UsersReq param = new UsersReq(clientId, null, null, null, null);		
		log.info("Body: {}", param.toString());
		
		List<UserDTO> response = userProxy.getUsers(authorizationHeader(), param);		
		List<UserDto> serviceDto = DtoMapper.userDtoMapper(response);		
		return serviceDto;
		
	}
	
	/**
	 * 사용자 정보 조회
	 * @param userId
	 * @return
	 */
	public UserDto getUser(String userId) {		
		log.info("Get user");
		
		UserDTO response = userProxy.getUser(authorizationHeader(), clientId, userId);		
		if(response != null) {
			return DtoMapper.userDtoMapper(response);
		}		
		
		return null;
	}
	
	/**
	 * 롤 리스트 조회
	 * @param clientId
	 */
	public List<AuthorityRequestDto.ReqRegistDto> getClientRoles(String clientId) {
		
		log.info("Search role list");
		log.info("Body: clientId = {}", clientId);
		
		List<ClientRoleDTO> response = roleProxy.getClientRoles(authorizationHeader(), clientId);
		
		List<AuthorityRequestDto.ReqRegistDto> serviceDto = DtoMapper.clientRoleDtoMapper(response);
		
		return serviceDto;
	}
	
	public List<ClientRoleDTO> getClientRole(String clientId) {
		
		List<ClientRoleDTO> response = roleProxy.getClientRoles(authorizationHeader(), clientId);
		
		return response;
	}
	
	/**
	 * 그룹 리스트 조회
	 */
	public List<ProjectRequestDto> getServiceGroups() {
		
		log.info("Search group list");
		
		GroupReq param = new GroupReq(clientId, null);
		
		log.info("Body: {}", param.toString());
		
		List<GroupDTO> response = groupProxy.getGroups(authorizationHeader(), param);
		
		List<ProjectRequestDto> serviceDto = DtoMapper.groupDtoMapper(response);
		
		return serviceDto;
	}
	
	/**
	 * 그룹별 사용자 리스트 조회
	 */
	public List<ProjectRequestDto> getServiceGroupDetail() {
		
		log.info("Search group detail");
		
		List<ProjectRequestDto> serviceDto = new ArrayList<>();
		
		// 그룹 리스트 조회
		GroupReq groupReq = new GroupReq(clientId, null);
		List<GroupDTO> response = groupProxy.getGroups(authorizationHeader(), groupReq);		
		
		// 그룹별 디테일 조회
		for(GroupDTO groupDTO : response) {
			GroupDetailReq detailParam = new GroupDetailReq(clientId, groupDTO.getUuid());

			log.info("Body: {}", detailParam.toString());
			
			GroupDetailDTO detailResponse = groupProxy.getGroupDetail(authorizationHeader(), detailParam);
			
			ProjectRequestDto serviceDTO = DtoMapper.groupDetailDtoMapper(detailResponse);
			
			serviceDto.add(serviceDTO);
		}
		return serviceDto;
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
