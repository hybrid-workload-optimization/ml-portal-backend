package kr.co.strato.portal.setting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.model.KeycloakRole;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDtoMapper;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.model.UserRoleDtoMapper;

@Service
public class UserService {
	
	@Autowired
	UserDomainService userDomainService;
	
	@Autowired
	UserRoleDomainService userRoleDomainservice;
	
	@Autowired
	KeyCloakApiUtil	keyCloakApiUtil;
	
	//등록
	public String postUser(UserDto param) {
		
		
		//keycloak 연동
		try {
			System.out.println("keycloak 연동 >> 등록");
			keyCloakApiUtil.createSsoUser(param);
		} catch (Exception e) {
			e.printStackTrace();
		}		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);
		userDomainService.saveUser(entity, "post");
		
		return param.getUserId();
	}
	
	//수정
	public String patchUser(UserDto param) {
		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);
		
		userDomainService.saveUser(entity, "patch");
		
		//keycloak 연동
		try {
			keyCloakApiUtil.updateSsoUser(param, null);
			System.out.println("keycloak 연동 >> 수정");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return param.getUserId();
	}
	
	//삭제
	public Long deleteUser(UserDto param) {
		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);

		userDomainService.deleteUser(entity);

		//keycloak 연동
		try {
			keyCloakApiUtil.updateSsoUser(param, null);
			System.out.println("keycloak 연동 >> 삭제");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return 0L;
	}
	
	//목록
	public Page<UserDto> getAllUserList(Pageable pageable) throws Exception{
		Page<UserEntity> userEntityList = userDomainService.getAllUserList(pageable);
		List<UserDto> userDtoList = userEntityList
										.getContent()
										.stream()
										.map(u -> UserDtoMapper.INSTANCE.toDto(u))
										.collect(Collectors.toList());
		
		return new PageImpl<>(userDtoList, pageable, userEntityList.getTotalElements());
	}
	
	//상세
	public UserDto getUserInfo(String userId) {
		UserEntity userEntity = userDomainService.getUserInfoByUserId(userId);
		System.out.println(userEntity.toString());
		UserDto userDto = UserDtoMapper.INSTANCE.toDto(userEntity);
		
		return userDto;
	}

	// User Role List
	public List<UserRoleDto> getUserRoleList(){
		List<UserRoleEntity> list =  userRoleDomainservice.getAllListAuthority();
		List<UserRoleDto> roleList = list
									.stream()
									.map(r -> UserRoleDtoMapper.INSTANCE.toDto(r))
									.collect(Collectors.toList());
		return roleList;
	}
	
	// 비밀번호 변경
	public void patchUserPassword(UserDto param) {

		try {
			keyCloakApiUtil.updatePasswordSsoUser(param, null);
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	//테스트용
	public void getTest() {

		try {
			//0. 관리자 토큰 생성
			String token = keyCloakApiUtil.getTokenByManager();
			
			/** USER **/
			//0. 테스트용 유저 생성
			UserDto.UserRole role = new UserDto.UserRole(null, "PROJECT MEMBER", null, null, null, null);
			UserDto user = new UserDto("test1", "test1@test.com", "test1@test.com", null, null, role);
			
			//1. 유저 생성
			keyCloakApiUtil.createSsoUser(user);
			
			//2. 유저 비밀번호 수정
			user.setUserPassword("test123");
			keyCloakApiUtil.updatePasswordSsoUser(user, token);
			
			//3. 유저 정보 조회
			keyCloakApiUtil.getUserInfoByUserId(user.getUserId());
			
			//4. 유저 토큰 생성
			
			//5. 유저 토큰 Refresh
			
			/** ROLE **/
			//전체 ROLE 가져오기
			keyCloakApiUtil.getRoleList();
			

			
			// 유저 ROLE 추가하기
//			KeycloakRole role = new KeycloakRole();
//			role.setId("d1f29139-d14e-42c5-9025-a36a02026336");
//			role.setName("proj_member");
//			role.setDescription("프로젝트 멤버");
//			role.setComposite(false);
//			role.setClientRole(false);
//			role.setContainerId("Strato-Cloud");
//			keyCloakApiUtil.postUserRole(user, token, role);
			
			//유저의  ROLE 가져오기
			keyCloakApiUtil.getUserRoleInfo(user, token);
			
			//유저 ROLE 추가
			
//			//유저 ROLE 삭제
//			keyCloakApiUtil.deleteUserRole(user, token, role);
			
			//유저의  ROLE 가져오기
			keyCloakApiUtil.getUserRoleInfo(user, token);
			
			
			// User 삭제
		
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	
	

}
