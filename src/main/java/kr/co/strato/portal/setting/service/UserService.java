package kr.co.strato.portal.setting.service;

import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.error.exception.SsoConnectionException;
import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDtoMapper;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.model.UserRoleDtoMapper;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
	@Autowired
	UserDomainService userDomainService;
	
	@Autowired
	UserRoleDomainService userRoleDomainservice;
	
	@Autowired
	KeyCloakApiUtil	keyCloakApiUtil;
	
	@Autowired
	TokenValidator tokenValidator;
	
	
	//등록
	public String postUser(UserDto param) {
		
		//keycloak 연동
		try {
			keyCloakApiUtil.createSsoUser(param);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new SsoConnectionException(e.getMessage());
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
		}  catch (Exception e) {
			log.error(e.getMessage());
		}
		
		return param.getUserId();
	}
	
	//삭제
	public Long deleteUser(UserDto param) {
		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);

		userDomainService.deleteUser(entity);

		//keycloak 연동
		try {
			keyCloakApiUtil.deleteSsoUser(param);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		
		return 0L;
	}
	
	//목록 > Page 만, 
	public Page<UserDto> getAllUserList(Pageable pageable) throws Exception{
		Page<UserEntity> userEntityList = userDomainService.getAllUserList(pageable);
		List<UserDto> userDtoList = userEntityList
										.getContent()
										.stream()
										.map(u -> UserDtoMapper.INSTANCE.toDto(u))
										.collect(Collectors.toList());
		
		return new PageImpl<>(userDtoList, pageable, userEntityList.getTotalElements());
	}
	
	// 목록 > param(projectId, authorityId)
	public Page<UserDto> getAllUserList(Pageable pageable, UserDto.SearchParam param) throws Exception{
		Page<UserEntity> userEntityList = userDomainService.getAllUserList(pageable, param);
		for (UserEntity userEntity : userEntityList) {
			System.out.println(userEntity.toString());
		}
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
			keyCloakApiUtil.updatePasswordSsoUser(param);
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		
	}

	//테스트용
	public void getTest() {

		try {
			//0. 관리자 토큰 생성
//			System.out.println("==== 관리자 토큰 생성 TEST");
//			String token = keyCloakApiUtil.getTokenByManager();
			
			/** USER **/
			//0. 테스트용 유저 생성
			
			UserDto.UserRole role = new UserDto.UserRole(null, "PROJECT MEMBER", null, null, null, null);
			UserDto user = new UserDto("test1@test.com", "test1@test.com", "test1@test.com", null, null, role);
			
			//1. 유저 생성
			System.out.println("==== 유저 생성 TEST");
//			keyCloakApiUtil.createSsoUser(user);
			
			System.out.println("==== 유저 비밀번호 변경 TEST");
			//2. 유저 비밀번호 수정
			user.setUserPassword("test1234");
//			keyCloakApiUtil.updatePasswordSsoUser(user);
			
			
			System.out.println("==== 유저 정보 조회 TEST");
			//3. 유저 정보 조회
			keyCloakApiUtil.getUserInfoByUserId(user.getUserId());
			
			
			System.out.println("==== 유저 토큰 생성 TEST");
			//4. 유저 토큰 생성
			ResponseEntity<KeycloakToken> data = keyCloakApiUtil.getTokenByUser(user);
//			KeycloakToken userToken = keyCloakApiUtil.getTokenByUser(user);
			
			KeycloakToken userToken = data.getBody();
			
			KeycloakToken tk = new KeycloakToken();
			tk.setRefreshToken(userToken.getRefreshToken());
			
			//4.1 토큰 유효성 검증
			tokenValidator.validateToken(tk.getAccessToken());
			
			System.out.println("==== 유저 토큰 갱신 TEST");
			//5. 유저 토큰 Refresh
			keyCloakApiUtil.refreshTokenByUser(tk);
			
			System.out.println("==== 전체 ROLE 조회 TEST");
			/** ROLE **/
			//전체 ROLE 가져오기
//			keyCloakApiUtil.getRoleList();
			

			
			// 유저 ROLE 추가하기
//			KeycloakRole role = new KeycloakRole();
//			role.setId("d1f29139-d14e-42c5-9025-a36a02026336");
//			role.setName("proj_member");
//			role.setDescription("프로젝트 멤버");
//			role.setComposite(false);
//			role.setClientRole(false);
//			role.setContainerId("Strato-Cloud");
//			keyCloakApiUtil.postUserRole(user, token, role);
			
			System.out.println("==== 특정 유저 ROLE 조회 TEST");
			//유저의  ROLE 가져오기
//			keyCloakApiUtil.getUserRoleInfo(user, token);
			
			//유저 ROLE 추가
			
//			//유저 ROLE 삭제
//			keyCloakApiUtil.deleteUserRole(user, token, role);
			
			//유저의  ROLE 가져오기
//			keyCloakApiUtil.getUserRoleInfo(user, token);
			
			
			System.out.println("==== 유저 삭제 TEST");
			// User 삭제
//			keyCloakApiUtil.deleteSsoUser(user);
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void tokenTest() {
//		String token = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJRVFMzT1IyYXhpQklnWWtSYlhCR2tCZVIybW5uOW1OSDQ4UWFXZEV4U053In0.eyJleHAiOjE2NDg1MTU5ODAsImlhdCI6MTY0ODUxNTY4MCwianRpIjoiNWZiYzYwYmMtMTg3OC00NWFiLTg5ZjktZWJmNTY5NDlmMWEwIiwiaXNzIjoiaHR0cDovLzE3Mi4xNi4xMC4xMTQ6ODU4MC9hdXRoL3JlYWxtcy9zdHJhdG8tY2xvdWQiLCJhdWQiOiJhY2NvdW50Iiwic3ViIjoiNDg2YzA1OWYtYTA3MS00N2ZkLWI3YmUtNzNiZDJkMmRjYjYwIiwidHlwIjoiQmVhcmVyIiwiYXpwIjoicGFhc19wb3J0YWwiLCJzZXNzaW9uX3N0YXRlIjoiMjAwZWU0MDUtNWMyZS00ODM0LThmYjEtY2UzNTE1ZTBiY2U4IiwiYWNyIjoiMSIsImFsbG93ZWQtb3JpZ2lucyI6WyJodHRwOi8vcGFhc3BvcnRhbC5zdHJhdG8uY29tIl0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJwcm9qX21lbWJlciIsIm9mZmxpbmVfYWNjZXNzIiwiZGVmYXVsdC1yb2xlcy1zcHRlay1jbG91ZCIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJwcm9maWxlIGVtYWlsIiwic2lkIjoiMjAwZWU0MDUtNWMyZS00ODM0LThmYjEtY2UzNTE1ZTBiY2U4IiwiZW1haWxfdmVyaWZpZWQiOmZhbHNlLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiJ0ZXN0MUB0ZXN0LmNvbSIsImVtYWlsIjoidGVzdDFAdGVzdC5jb20ifQ.P7Kn6gQiQAeCBoF-7fTKuX3lOgCZ1GMlBhL2I1eR5JQK8SPzdE1F1eoL2rBSZaTuTtgeqLGKyB6qN7GHkpOj8tLpO04UCxij5eii00V_uGZqaqLg141o4OJUcCM5bAXo0WKFrKy6EFF6NVQQye4xXRz73yeZZDi3fqSKY4VKXURXXJ5Olx2E9C2eZR5YRsX6mq6fey3iDQIlTcwkOZ5EjFk8KeDnUn8-9KF0YAo565LoCsAyk8aHXdkVOErTDfPBFHkBjLrik7PLl7vGxPIT9f68Gg_XE3OkeQoS5QffgyLGKlzfhO_Ipblo1ZI3SSyt1VxbEtt5RVaIa3jRD6t96A";
//		tokenValidator.validateToken(token);
	}


	
	

}
