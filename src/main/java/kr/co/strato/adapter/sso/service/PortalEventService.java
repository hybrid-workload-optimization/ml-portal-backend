package kr.co.strato.adapter.sso.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import kr.co.strato.adapter.sso.common.DtoMapper;
import kr.co.strato.adapter.sso.model.MessageModel;
import kr.co.strato.adapter.sso.model.dto.ClientRoleDTO;
import kr.co.strato.adapter.sso.model.dto.GroupDTO;
import kr.co.strato.adapter.sso.model.dto.UserDTO;
import kr.co.strato.adapter.sso.model.dto.UserEventDTO;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.service.PortalProjectService;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDto.ReqDeleteDto;
import kr.co.strato.portal.setting.model.UserDto;
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
	
	@Value("${auth.client.client-id}")
	private String clientId;
	
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
		
//		UserDTO userDTO = gson.fromJson(dataStr, UserDTO.class);
		UserEventDTO userDTO = gson.fromJson(dataStr, UserEventDTO.class);
		UserDto serviceDto = DtoMapper.userDtoMapper(userDTO);
		
		log.info(dataStr);
		log.info(eventName);
		
		if(EVENT_CREATED.equals(eventName)) {
			
			UserDto loginUser = new UserDto();
			loginUser.setCreateUserName(userDTO.getCreatedBy());

			String result = userService.postUser(serviceDto, loginUser);
			log.info("created user >>>> {}", result);
		
		} else if(EVENT_REMOVED.equals(eventName)) {
			
			Long result = userService.deleteUser(serviceDto);
			log.info("deleted user >>>> {}", result);
		
		} else if(EVENT_UPDATED.equals(eventName)) {
			
			serviceDto.setUpdateUserName(userDTO.getUpdatedBy());
			
			String result = userService.patchUser(serviceDto);
			log.info("updated user >>>> {}", result);
			
		}
	}
	

	
	/**
	 * User Role 변경 이벤트 처리
	 * updated
	 * @param messageObj
	 */
	public void changeUserRoleEvent(MessageModel messageObj) {
		
		Gson gson = new Gson();
		String eventName = messageObj.getEvent();
		Object user = messageObj.getUser();
		String dataStr = gson.toJson(user);
		
		UserDTO userDTO = gson.fromJson(dataStr, UserDTO.class);
		
		UserDto serviceDto = DtoMapper.userDtoMapper(userDTO);
		
		log.info(dataStr);
		log.info(eventName);

		serviceDto.setUpdateUserName(userDTO.getUpdatedBy());
		
		String result = userService.userRoleUpdate(serviceDto);
		
		log.info("updated user >>>> {}", result);
		
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
		
		// 보류 : 모든 클라이언트에 서비스 그룹 생성하고 추후 변경 예정
//		List<String> allowClients = groupDTO.getAllowClients();
//		if(allowClients != null && !allowClients.contains(clientId)) {
//			이 클라이언트에 허용되는 서비스 그룹이 아닌 경우 패스!
//			log.info("Service Group Event - 이 클라이언트에 지원되는 서비스 그룹이 아닙니다. ClientId: {}, ServiceGroupName: {}", clientId, groupDTO.getGroupName());
//			return;
//		}

		ProjectRequestDto serviceDTO = DtoMapper.groupDtoMapper(groupDTO);
		
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
		
		ProjectRequestDto serviceDTO = DtoMapper.groupMemberDtoMapper(groupDTO, userDTO);
		
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
		
			AuthorityRequestDto.ReqRegistDto createRoleDto = DtoMapper.clientRoleDtoMapper(roleDTO);
			
			Long result = authorityService.postUserRole(createRoleDto);
			log.info("created role >>>> {}", result);
			
		} else if(EVENT_REMOVED.equals(eventName)) {
			
			AuthorityRequestDto.ReqDeleteDto removeRoleDto = new ReqDeleteDto();
			removeRoleDto.setUserRoleName(roleDTO.getRoleName());
			
			Long result = authorityService.deleteUserRole(removeRoleDto);
			log.info("remove role >>>> {}", result);
			
		}
	}
	
}
