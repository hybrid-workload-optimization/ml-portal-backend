package kr.co.strato.adapter.sso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;

import kr.co.strato.adapter.sso.model.ClientRoleDTO;
import kr.co.strato.adapter.sso.model.MessageModel;
import kr.co.strato.adapter.sso.model.UserDTO;
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
	
	public static final String EVENT_CREATED = "created";
    public static final String EVENT_REMOVED = "removed";
    public static final String EVENT_UPDATED = "updated";
    public static final String EVENT_JOIN_GROUP = "join_group";
    public static final String EVENT_LEAVE_GROUP = "leave_group";
    
	
	/**
	 * User 이벤트 처리
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
		
		// 유저 생성
		if(EVENT_CREATED.equals(eventName)) {
			
			UserDto loginUser = new UserDto();
			loginUser.setUserName(userDTO.getCreatedBy());

			String result = userService.postUser(serviceDto, loginUser);
			log.info("created user >>>> {}", result);
		
		// 유저 삭제
		} else if(EVENT_REMOVED.equals(eventName)) {
			
			Long result = userService.deleteUser(serviceDto);
			log.info("deleted user >>>> {}", result);
		
		// 유저 수정
		} else if(EVENT_UPDATED.equals(eventName)) {
			
			serviceDto.setUpdateUserName(userDTO.getUpdateBy());
			
			String result = userService.patchUser(serviceDto);
			log.info("updated user >>>> {}", result);
			
		}
	}
	
	
	/**
	 * Role 이벤트 처리
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
