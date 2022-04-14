package kr.co.strato.portal.setting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserResetPasswordEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.error.exception.SsoConnectionException;
import kr.co.strato.global.model.KeycloakToken;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.ResetParam;
import kr.co.strato.portal.setting.model.UserDto.ResetRequestResult;
import kr.co.strato.portal.setting.model.UserDtoMapper;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.model.UserRoleDtoMapper;
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
	
	@Autowired
	EmailService emailService;
	
	@Value("${portal.front.service.url}")
	String frontUrl;
	
	@Value("${portal.backend.service.url}")
	String backUrl;
	
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
		
		//패스워드 초기화 이메일 전송
		requestResetPassword(param.getUserId(), param.getEmail());
		return param.getUserId();
	}
	
	/**
	 * 패스워드 변경 요청
	 * @param user
	 */
	private void requestResetPassword(String userId, String email) {		
		String requestCode = UUID.randomUUID().toString();

		//변경 요청 생성.
		UserResetPasswordEntity requestEntity = new UserResetPasswordEntity();
		requestEntity.setRequestCode(requestCode);
		requestEntity.setEmail(email);
		requestEntity.setUserId(userId);
		userDomainService.saveResetPasswordRequest(requestEntity);
		
		
		
		String title = "PaaS Portal 비밀번호 재설정";
		String contents = genResetPasswordContents(requestCode);
		emailService.sendMail(email, title, contents);
	}
	
	/**
	 * 패스워드 변경 요청 이메일 반환.
	 * @param requestCode
	 * @return
	 */
	public String getRequestUserId(String requestCode) {
		UserResetPasswordEntity entity = userDomainService.getResetPasswordRequest(requestCode);
		if(entity != null) {
			return entity.getUserId();
		}
		return null;
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
	
	
	public ResetRequestResult requestResetPassword(String email) {
		ResetRequestResult result = new ResetRequestResult();
		UserEntity entity = userDomainService.getUserInfoByEmail(email);
		if(entity != null) {
			String userId = entity.getUserId();
			requestResetPassword(userId, email);
			
			result.setResult("success");
			result.setUserId(userId);
		} else {
			result.setResult("fail");
			result.setReason("unknown user");
		}
		return result;
	}


	public ResetRequestResult getResetUserId(String requestCode) {
		UserResetPasswordEntity entity = userDomainService.getResetPasswordRequest(requestCode);
		ResetRequestResult request = new ResetRequestResult();
		if(entity != null) {
			LocalDateTime requestTime = entity.getCreatedAt();
			LocalDateTime beforeOneHour = LocalDateTime.now().minusHours(1);
			
			if(beforeOneHour.isBefore(requestTime)) {
				//유효 시간 이내
				request.setResult("success");
				request.setUserId(entity.getUserId());
			} else {
				//1시간 경과
				request.setResult("fail");
				request.setReason("expiry");
			}
		} else {
			//잘못된 요청 페이지
			request.setResult("fail");
			request.setReason("bad request");
		}		
		return request;
	}	
	
	/**
	 * 패스워드 변경
	 * @param param
	 */
	public String resetUserPassword(ResetParam param) {
		String requestCode = param.getRequestCode();
		String id = param.getUserId();
		String password = param.getUserPassword();
		
		String result = null;
		UserResetPasswordEntity entity = userDomainService.getResetPasswordRequest(requestCode);
		if(entity != null) {
			if(entity.getUserId().equals(param.getUserId())) {
				LocalDateTime requestTime = entity.getCreatedAt();
				LocalDateTime beforeOneHour = LocalDateTime.now().minusHours(1);
				
				if(beforeOneHour.isBefore(requestTime)) {
					UserDto user = new UserDto();
					user.setUserId(id);
					user.setUserPassword(password);
					
					patchUserPassword(user);
					userDomainService.deleteResetPasswordRequest(id);
					
					result = "success";
				} else {
					//1시간 경과
					result = "expiry";
				}
			} else {
				result = "bad request";
			}
		} else {
			//잘못된 요청
			result = "bad request";
		}
		return result;
	}
	
	
	/**
	 * 비밀번호 초기화 html 내용 생성하여 리턴.
	 * @param requestCode
	 * @return
	 */
	private String genResetPasswordContents(String requestCode) {
		String url = frontUrl;
		if(!url.endsWith("/")) {
			url += "/";
		}		
		url += "change-password?requestCode="+requestCode;
		return url;
	}
	

	/*
	public String getResetPasswordUrl(String requestCode) {
		UserResetPasswordEntity entity = userDomainService.getResetPasswordRequest(requestCode);		
		String url = frontUrl;
		if(!url.endsWith("/")) {
			url += "/";
		}
		
		if(entity != null) {
			LocalDateTime requestTime = entity.getCreatedAt();
			LocalDateTime beforeOneHour = LocalDateTime.now().minusHours(1);
			
			if(beforeOneHour.isBefore(requestTime)) {
				//유효 시간 이내
				url += "change-password?userId="+entity.getUserId()+"&requestCode="+requestCode;
			} else {
				//1시간 경과
				url += "change-password-expiry";
			}
		} else {
			//잘못된 요청 페이지
			url += "bad-request";
		}		
		return url;
	}
	*/
	

}
