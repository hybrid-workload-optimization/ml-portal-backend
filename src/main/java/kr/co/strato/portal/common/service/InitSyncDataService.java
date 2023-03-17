package kr.co.strato.portal.common.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.adapter.sso.service.PortalAdapterService;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.domain.project.repository.ProjectRepository;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.domain.user.repository.UserRepository;
import kr.co.strato.domain.user.repository.UserRoleMenuRepository;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.global.error.exception.SyncGroupFailException;
import kr.co.strato.global.error.exception.SyncRoleFailException;
import kr.co.strato.global.error.exception.SyncUserFailException;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.service.PortalProjectService;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.service.AuthorityService;
import kr.co.strato.portal.setting.service.UserService;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class InitSyncDataService {

	@Autowired
	PortalAdapterService portalAdapterService;
	
	@Autowired
	PortalProjectService projectService;
	
	@Autowired
	AuthorityService authorityService;
	
	@Autowired
	UserService userService;
	
	@Autowired
	UserRoleRepository roleRepository;
	@Autowired
	UserRepository userRepository;
	@Autowired
	UserRoleMenuRepository roleMenuRepository;
	@Autowired
	ProjectRepository projectRepository;
	@Autowired
	ProjectUserRepository projectUserRepository;
	@Autowired
	ProjectClusterRepository projectClusterRepository;
	
	
	
	@Value("${auth.clientId}")
	String clientId;
	
	/**
	 *  1. role 데이터 동기화
	 *  - user_role, user_role_menu
	 */
	@Transactional
	public boolean syncRoleData() {
		
		try {
			
			if(roleRepository.count() < 1) {
				
				log.info("Sync role data start...");
				
				List<AuthorityRequestDto.ReqRegistDto> roleList = portalAdapterService.getClientRoles(clientId);
				
				for(AuthorityRequestDto.ReqRegistDto role : roleList) {
	
					Long resultIdx = authorityService.postUserRole(role);
					
					log.info("result idx: {}", resultIdx);

				}
				
				log.info("Sync role data end...");
				
			} else {
				log.info("Role data already exists.");
			}
			
		} catch(Exception e) {
			log.error("", e);
			throw new SyncRoleFailException();
		}
		
		return true;
		
	}
	
	/**
	 *  2. user 데이터 동기화
	 */
	@Transactional
	public boolean syncUserData() {		
		try {	
			if(userRepository.count() < 1) {				
				log.info("Sync user data start...");				
				List<UserDto> userList = portalAdapterService.getUsers();				
				for(UserDto user : userList) {					
					UserDto loginUser = new UserDto();
					loginUser.setUserName(user.getCreateUserName());
					String resultId = userService.postUser(user, loginUser);
					
					log.info("result id: {}", resultId);
				}
				log.info("Sync user data end...");
			} else {
				log.info("User data already exists.");
			}
		} catch(Exception e) {
			log.error("", e);
			throw new SyncUserFailException();
		}
		return true;
	}
	
	public boolean syncUserData(String userId) {	
		log.info("Sync user data start... userId: {}", userId);				
		UserDto user = portalAdapterService.getUser(userId);
		if(user != null) {
			UserDto loginUser = new UserDto();
			loginUser.setUserName(user.getCreateUserName());
			String resultId = userService.postUser(user, loginUser);
			log.info("result id: {}", resultId);
			return true;
		} 
		return false;
	}

	/**
	 *  3. Group(project) 데이터 동기화
	 *  - project, project_user
	 */
	@Transactional
	public boolean syncGroupData() {		
		try {	
			if(projectRepository.count() < 1) {				
				log.info("Sync group data start...");				
				List<ProjectRequestDto> projectDetail = portalAdapterService.getServiceGroupDetail();				
				for(ProjectRequestDto project : projectDetail) {
					
					//사용자 존재 유무 확인 후 없을 경우 동기화					
					List<ProjectUserDto> userList = new ArrayList<>();
					userList.addAll(project.getUserList());
					userList.add(project.getProjectManager());
					for(ProjectUserDto u : userList) {
						String userId = u.getUserId();
						if(!userService.isExistUser(userId)) {
							syncUserData(userId);
						}
					}
					
					Long resultIdx = projectService.createProject(project);
					log.info("result idx: {}", resultIdx);
				}
				log.info("Sync group data end...");
			} else {
				log.info("Group data already exists.");
			}
		} catch(Exception e) {
			log.error("", e);
			throw new SyncGroupFailException();
		}
		return true;
		
	}


	// 동기화 테스트 용도
	// 기존 DB 데이터 삭제하여 동기화 테스트 진행시 사용
	public void syncDataDelete() {
		roleMenuRepository.deleteAllInBatch();
		roleRepository.deleteAllInBatch();
		projectClusterRepository.deleteAllInBatch();
		projectUserRepository.deleteAllInBatch();
		projectRepository.deleteAllInBatch();
		userRepository.deleteAllInBatch();		
	}
	
}
