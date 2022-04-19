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
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserResetPasswordEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.error.exception.SsoConnectionException;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.global.validation.TokenValidator;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.ResetParam;
import kr.co.strato.portal.setting.model.UserDto.ResetRequestResult;
import kr.co.strato.portal.setting.model.UserDto.UserRole;
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
	
	@Autowired
	KeyCloakApiUtil keycloakApiUtil;
	
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
		String userId = param.getUserId();
		
		//keycloak 연동
		try {			
			//updateSsoUser에서는 UserId, Email을 변경하고 있는데 
			//두 정보는 변경 여지가 없기 때문에 주석 처리
			//이호철 22.04.19
			//keyCloakApiUtil.updateSsoUser(param, null);
			
			
			UserEntity old = userDomainService.getUserInfoByUserId(param.getUserId());
			UserRoleEntity oldRole = old.getUserRole();
			UserRole newRole = param.getUserRole();
			
			if(newRole == null) {
				keycloakApiUtil.deleteAllUserRole(userId);
				keycloakApiUtil.logoutUser(userId);
			} else {
				if(!oldRole.getUserRoleCode().equals(newRole.getUserRoleCode())) {
					//권한이 변경된 경우 Keycloak에 변경사항 반영
					keycloakApiUtil.postUserRole(userId, newRole.getUserRoleCode());
					keycloakApiUtil.logoutUser(userId);
				}
			}
			
		}  catch (Exception e) {
			log.error(e.getMessage());
		}
		
		
		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);
		
		userDomainService.saveUser(entity, "patch");
				
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
	
	/*
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
	*/
	
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
		url += "#/change-password?requestCode="+requestCode;
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
