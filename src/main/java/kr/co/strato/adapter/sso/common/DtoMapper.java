package kr.co.strato.adapter.sso.common;

import java.util.ArrayList;
import java.util.List;

import kr.co.strato.adapter.sso.model.dto.ClientRoleDTO;
import kr.co.strato.adapter.sso.model.dto.GroupDTO;
import kr.co.strato.adapter.sso.model.dto.GroupDetailDTO;
import kr.co.strato.adapter.sso.model.dto.UserDTO;
import kr.co.strato.adapter.sso.model.dto.UserEventDTO;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDto.ReqRegistDto;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.UserRole;

/**
 * SSO Event DTO -> Portal Service DTO 변환
 */
public class DtoMapper {

	public static AuthorityRequestDto.ReqRegistDto clientRoleDtoMapper(ClientRoleDTO roleDTO) {

		AuthorityRequestDto.ReqRegistDto serviceDTO = new ReqRegistDto();
		serviceDTO.setUserRoleName(roleDTO.getRoleName());
		serviceDTO.setDescription(roleDTO.getDescription());
		serviceDTO.setParentUserRoleIdx(0L);
		serviceDTO.setGroupYn("N");
		
		String roleCode = roleDTO.getRoleName().toUpperCase().replace(" ", "_");
		
		serviceDTO.setUserRoleCode(roleCode);
		
		return serviceDTO;
	}
	
	public static List<AuthorityRequestDto.ReqRegistDto> clientRoleDtoMapper(List<ClientRoleDTO> rolesDTO) {

		List<AuthorityRequestDto.ReqRegistDto> serviceDTO = new ArrayList<>();
		
		for(ClientRoleDTO roleDTO : rolesDTO) {			
			AuthorityRequestDto.ReqRegistDto serviceDto = clientRoleDtoMapper(roleDTO);
			serviceDTO.add(serviceDto);
		}
		
		return serviceDTO;
	}
	
	public static UserDto userDtoMapper(UserEventDTO userDTO) {
		
		UserDto serviceDto = new UserDto(); 
		UserRole userRole = null;
		serviceDto.setUserId(userDTO.getUserId());
		if(userDTO.getUsernameEn() != null) {
			serviceDto.setUserName(userDTO.getUsernameEn());
		} else if(userDTO.getUsernameKr() != null ) {
			serviceDto.setUserName(userDTO.getUsernameKr());
		} else {
			serviceDto.setUserName("non");
		}
		serviceDto.setEmail(userDTO.getEmail());
		serviceDto.setOrganization(userDTO.getOrganization());
		serviceDto.setContact(userDTO.getContact());
		if(userDTO.getEnabled())  {
			serviceDto.setUseYn("Y");
		} else {
			serviceDto.setUseYn("N");
		}

		serviceDto.setUserRole(userRole);
		
		return serviceDto;
	}

	public static UserDto userDtoMapper(UserDTO userDTO) {
		
		UserDto serviceDto = new UserDto(); 
		UserRole userRole = null;
		serviceDto.setUserId(userDTO.getUserId());
		if(userDTO.getUsernameEn() != null) {
			serviceDto.setUserName(userDTO.getUsernameEn());
		} else if(userDTO.getUsernameKr() != null ) {
			serviceDto.setUserName(userDTO.getUsernameKr());
		} else {
			serviceDto.setUserName("non");
		}
		serviceDto.setEmail(userDTO.getEmail());
		serviceDto.setOrganization(userDTO.getOrganization());
		serviceDto.setContact(userDTO.getContact());
		if(userDTO.getEnabled())  {
			serviceDto.setUseYn("Y");
		} else {
			serviceDto.setUseYn("N");
		}

		if(userDTO.getRoles() != null && userDTO.getRoles().size() > 0) {
			String roleName = userDTO.getRoles().get(0);
			userRole = new UserRole();
			userRole.setUserRoleName(roleName);
		}
		
		serviceDto.setUserRole(userRole);
		
		return serviceDto;
	}
	
	public static List<UserDto> userDtoMapper(List<UserDTO> usersDTO) {
		
		List<UserDto> serviceDTO = new ArrayList<>();
		
		for(UserDTO userDTO : usersDTO) {
			UserDto serviceDto = userDtoMapper(userDTO);
			serviceDTO.add(serviceDto);
		}
		
		return serviceDTO;
	}
	
	public static ProjectRequestDto groupMemberDtoMapper(GroupDTO groupDTO, UserDTO userDTO) {

		ProjectUserDto userDto = new ProjectUserDto();
		userDto.setUserId(userDTO.getUserId());
		userDto.setCreateUserName(userDTO.getCreatedBy());
		userDto.setUserRoleCode("PROJECT_MEMBER");
		List<ProjectUserDto> userList = new ArrayList<>();
		userList.add(userDto);

		ProjectRequestDto serviceDTO = new ProjectRequestDto();
		serviceDTO.setProjectName(groupDTO.getGroupName());
		serviceDTO.setDescription(groupDTO.getDescription());
		serviceDTO.setLoginName(groupDTO.getCreatedBy());
		serviceDTO.setUserList(userList);
		
		return serviceDTO;
	}
	
	public static ProjectRequestDto groupDtoMapper(GroupDTO groupDTO) {

		// Project Manager 추가
		ProjectUserDto userDto = new ProjectUserDto();
		userDto.setUserId(groupDTO.getManagerId());
		userDto.setCreateUserName(groupDTO.getCreatedBy());
		userDto.setUserRoleCode("PROJECT_MANAGER");
		List<ProjectUserDto> userList = new ArrayList<>();
		userList.add(userDto);

		ProjectRequestDto serviceDTO = new ProjectRequestDto();
		serviceDTO.setProjectName(groupDTO.getGroupName());
		serviceDTO.setDescription(groupDTO.getDescription());
		serviceDTO.setUserList(userList);
		serviceDTO.setUuid(groupDTO.getUuid());
		
		return serviceDTO;
	}
	
	public static List<ProjectRequestDto> groupDtoMapper(List<GroupDTO> groupsDTO) {

		List<ProjectRequestDto> serviceDTO = new ArrayList<>();
		
		for(GroupDTO groupDTO : groupsDTO) {
			ProjectRequestDto serviceDto = groupDtoMapper(groupDTO);
			serviceDTO.add(serviceDto);
		}
		
		return serviceDTO;
	}
	
	public static ProjectRequestDto groupDetailDtoMapper(GroupDetailDTO groupDTO) {

		List<ProjectUserDto> userList = new ArrayList<>();

		// Project Manager
		ProjectUserDto managerDto = new ProjectUserDto();
		managerDto.setUserId(groupDTO.getManagerId());
		managerDto.setUserRoleCode("PROJECT_MANAGER");
		userList.add(managerDto);
		
		// Project Member
		for(UserDTO userDTO : groupDTO.getMembers()) {
			ProjectUserDto userDto = new ProjectUserDto();
			
			userDto.setUserId(userDTO.getUserId());
			userDto.setCreateUserName(userDTO.getCreatedBy());
			userDto.setUserRoleCode("PROJECT_MEMBER");
			userList.add(userDto);
		}

		ProjectRequestDto serviceDTO = new ProjectRequestDto();
		serviceDTO.setProjectName(groupDTO.getGroupName());
		serviceDTO.setDescription(groupDTO.getDescription());
		serviceDTO.setUuid(groupDTO.getUuid());
		serviceDTO.setProjectManager(managerDto);
		serviceDTO.setUserList(userList);
		
		return serviceDTO;
	}
	

}
