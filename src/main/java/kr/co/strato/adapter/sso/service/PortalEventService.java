package kr.co.strato.adapter.sso.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import kr.co.strato.adapter.sso.model.ClientRoleDTO;
import kr.co.strato.adapter.sso.model.GroupDTO;
import kr.co.strato.adapter.sso.model.MessageModel;
import kr.co.strato.adapter.sso.model.UserDTO;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.service.PortalProjectService;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDto.ReqDeleteDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDto.ReqRegistDto;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.UserRole;
import kr.co.strato.portal.setting.service.AuthorityService;
import kr.co.strato.portal.setting.service.UserService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PortalEventService {

	@Autowired
	private UserService userService;
	@Autowired
	private AuthorityService authorityService;
	@Autowired
	private PortalProjectService projectService;
	
	public static final String EVENT_CREATED = "created";
    public static final String EVENT_REMOVED = "removed";
    public static final String EVENT_UPDATED = "updated";
    public static final String EVENT_JOIN_GROUP = "join_group";
    public static final String EVENT_LEAVE_GROUP = "leave_group";
    
	
	/**
	 * User 이벤트 처리
	 * created / removed / updated
	 * @param messageObj
	 */
	public void userEvent(MessageModel messageObj) {
		
		Gson gson = new Gson();
		String eventName = messageObj.getEvent();
		Object data = messageObj.getData();
		String dataStr = gson.toJson(data);		
		
		UserDTO userDTO = gson.fromJson(dataStr, UserDTO.class);
		// 이벤트 DTO -> 서비스 DTO 데이터 파싱
		UserDto serviceDto = userDtoMapper(userDTO);
		
		log.info(data.toString());
		log.info(eventName);
		
		if(EVENT_CREATED.equals(eventName)) {
			
			UserDto loginUser = new UserDto();
			loginUser.setUserName(userDTO.getCreatedBy());

			String result = userService.postUser(serviceDto, loginUser);
			log.info("created user >>>> {}", result);
		
		} else if(EVENT_REMOVED.equals(eventName)) {
			
			Long result = userService.deleteUser(serviceDto);
			log.info("deleted user >>>> {}", result);
		
		} else if(EVENT_UPDATED.equals(eventName)) {
			
			serviceDto.setUpdateUserName(userDTO.getUpdateBy());
			
			String result = userService.patchUser(serviceDto);
			log.info("updated user >>>> {}", result);
			
		}
	}
	
	/**
	 * Group 이벤트 처리
	 * created / removed / updated
	 * @param messageObj
	 */
	public void groupEvent(MessageModel messageObj) {
		
		Gson gson = new Gson();
		String eventName = messageObj.getEvent();
		Object data = messageObj.getData();
		String dataStr = gson.toJson(data);		
		
		GroupDTO groupDTO = gson.fromJson(dataStr, GroupDTO.class);

		ProjectRequestDto serviceDTO = groupDtoMapper(groupDTO);
		
		log.info(data.toString());
		log.info(eventName);
		
		if(EVENT_CREATED.equals(eventName)) {
		
			serviceDTO.setLoginName(groupDTO.getCreatedBy());
			
			Long result = projectService.createProject(serviceDTO);
			log.info("created group >>>> {}", result);
		
		} else if(EVENT_REMOVED.equals(eventName)) {
			
			serviceDTO.setLoginName(groupDTO.getCreatedBy());
			
			boolean result = projectService.deleteProject(serviceDTO, null);
			log.info("deleted group >>>> {}", result);
		
		} else if(EVENT_UPDATED.equals(eventName)) {
			
			serviceDTO.setLoginName(groupDTO.getUpdatedBy());
			
			boolean result = projectService.updateProject(serviceDTO);
			log.info("updated group >>>> {}", result);
			
		}
		
	}
	
	/**
	 * Group 멤버 이벤트 처리
	 * join / leave
	 * @param messageObj
	 */
	public void groupMemberEvent(MessageModel messageObj) {
		
		Gson gson = new Gson();
		String eventName = messageObj.getEvent();
		Object group = messageObj.getGroup();
		Object user = messageObj.getUser();
		
		String groupStr = gson.toJson(group);
		String userStr = gson.toJson(user);
		
		GroupDTO groupDTO = gson.fromJson(groupStr, GroupDTO.class);
		UserDTO userDTO = gson.fromJson(userStr, UserDTO.class);
		
		ProjectRequestDto serviceDTO = groupMemberDtoMapper(groupDTO, userDTO);
		
		if(EVENT_JOIN_GROUP.equals(eventName)) {
			
			boolean result = projectService.updateProjectUser(serviceDTO);
			log.info("join group member >>>> {}", result);
			
		} else if(EVENT_LEAVE_GROUP.equals(eventName)) {
			
			String userId = serviceDTO.getUserList().get(0).getUserId();
			Long projectIdx = projectService.getProjectIdx(serviceDTO.getProjectName());
			boolean result = projectService.deleteProjectUser(projectIdx, userId);
			
			log.info("leave group member >>>> {}", result);
			
		}	
	}
	

	/**
	 * Role 이벤트 처리
	 * created / removed
	 * @param messageObj
	 */
	public void roleEvent(MessageModel messageObj) {
		
		Gson gson = new Gson();
		String eventName = messageObj.getEvent();
		Object role = messageObj.getRole();
		String roleStr = gson.toJson(role);
		
		ClientRoleDTO roleDTO = gson.fromJson(roleStr, ClientRoleDTO.class);
		
		if(EVENT_CREATED.equals(eventName)) {
		
			// 이벤트 DTO -> 서비스 DTO 데이터 파싱
			AuthorityRequestDto.ReqRegistDto createRoleDto = new ReqRegistDto();
			createRoleDto.setUserRoleName(roleDTO.getRoleName());
			createRoleDto.setDescription(roleDTO.getDescription());
			createRoleDto.setParentUserRoleIdx(0L);
			createRoleDto.setGroupYn("N");
			
			Long result = authorityService.postUserRole(createRoleDto);
			log.info("created role >>>> {}", result);
			
		} else if(EVENT_REMOVED.equals(eventName)) {
			
			AuthorityRequestDto.ReqDeleteDto removeRoleDto = new ReqDeleteDto();
			removeRoleDto.setUserRoleName(roleDTO.getRoleName());
			
			Long result = authorityService.deleteUserRole(removeRoleDto);
			log.info("remove role >>>> {}", result);
			
		}
	}
	
	
	// 이벤트 DTO -> 서비스 DTO 데이터 파싱
	public ProjectRequestDto groupMemberDtoMapper(GroupDTO groupDTO, UserDTO userDTO) {

		// Project Member 추가
		ProjectUserDto userDto = new ProjectUserDto();
		userDto.setUserId(userDTO.getUsername());
		userDto.setCreateUserName(userDTO.getCreatedBy());
		userDto.setUserRoleIdx(6L);
		userDto.setUserRoleName("PROJECT_MEMBER");
		List<ProjectUserDto> userList = new ArrayList<>();
		userList.add(userDto);

		ProjectRequestDto serviceDTO = new ProjectRequestDto();
		serviceDTO.setProjectName(groupDTO.getGroupName());
		serviceDTO.setDescription(groupDTO.getDescription());
		serviceDTO.setLoginName(groupDTO.getCreatedBy());
		serviceDTO.setUserList(userList);
		
		return serviceDTO;
	}
	
	public ProjectRequestDto groupDtoMapper(GroupDTO groupDTO) {

		// Project Manager 추가
		ProjectUserDto userDto = new ProjectUserDto();
		userDto.setUserId(groupDTO.getManager());
		userDto.setCreateUserName(groupDTO.getCreatedBy());
		userDto.setUserRoleIdx(5L);
		userDto.setUserRoleName("PROJECT_MANAGER");
		List<ProjectUserDto> userList = new ArrayList<>();
		userList.add(userDto);

		ProjectRequestDto serviceDTO = new ProjectRequestDto();
		serviceDTO.setProjectName(groupDTO.getGroupName());
		serviceDTO.setDescription(groupDTO.getDescription());
		serviceDTO.setUserList(userList);
		
		return serviceDTO;
	}
	
	public UserDto userDtoMapper(UserDTO userDTO) {
		
		UserDto serviceDto = new UserDto(); 
		UserRole userRole = new UserRole();
		serviceDto.setUserId(userDTO.getUsername());
		serviceDto.setUserName(userDTO.getLastName() + " " + userDTO.getFirstName());
		serviceDto.setEmail(userDTO.getEmail());
		serviceDto.setOrganization(userDTO.getCompanyName());
		serviceDto.setContact(userDTO.getContact());
		if(userDTO.getEnabled())  {
			serviceDto.setUseYn("Y");
		} else {
			serviceDto.setUseYn("N");
		}
		serviceDto.setUserRole(userRole);
		
		return serviceDto;
	}
}
