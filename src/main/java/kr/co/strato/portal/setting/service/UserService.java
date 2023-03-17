package kr.co.strato.portal.setting.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserResetPasswordEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.util.FileUtils;
import kr.co.strato.portal.common.service.AccessService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.EnableUserDto;
import kr.co.strato.portal.setting.model.UserDto.ResetParam;
import kr.co.strato.portal.setting.model.UserDto.ResetRequestResult;
import kr.co.strato.portal.setting.model.UserDtoMapper;
import kr.co.strato.portal.setting.model.UserRoleDto;
import kr.co.strato.portal.setting.model.UserRoleDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserService {
	
	private static final String INIT_PASSWORD_TEMPLATE_PATH = "classpath:/user/mail/init-password.html";
	
	@Autowired
	UserDomainService userDomainService;
	
	@Autowired
	UserRoleDomainService userRoleDomainservice;
	
	@Autowired
	EmailService emailService;
	
	@Autowired
	AccessService accessService;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Value("${portal.front.service.url}")
	String frontUrl;
	
	@Value("${portal.backend.service.url}")
	String backUrl;
	
	String INIT_PASSWORD_TEMPLATE;
	
	
	@PostConstruct
	public void init() {
		try {
			PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
			Resource res = resolver.getResource(INIT_PASSWORD_TEMPLATE_PATH);
			INIT_PASSWORD_TEMPLATE = FileUtils.readLine(res.getInputStream());
		} catch (Exception e) {
			log.error("", e);
		}
	}

	//등록
	public String postUser(UserDto param, UserDto loginUser) {
		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);
		
		entity.setCreateUserName(loginUser.getCreateUserName());
		
		if(param.getUserRole() != null) {
			String roleName = param.getUserRole().getUserRoleName();
			UserRoleEntity role = userRoleRepository.findTop1BByUserRoleName(roleName);
			
			entity.getUserRole().setId(role.getId());
			entity.getUserRole().setUserRoleCode(role.getUserRoleCode());
			
			entity.setUseYn("Y");
		} else {
			entity.setUseYn("N");
		}

		userDomainService.saveUser(entity, "post");
		
		return param.getUserId();
	}
	
	//수정
	public String patchUser(UserDto param) {

		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);	
		entity.setUpdateUserName(param.getUpdateUserName());
		
		userDomainService.saveUser(entity, "patch");
		
		return param.getUserId();
	}
	
	// 유저 롤 추가/삭제
	public String userRoleUpdate(UserDto param) {

		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);	
		entity.setUpdateUserName(param.getUpdateUserName());
		
		if(param.getUserRole() != null) {
			String roleName = param.getUserRole().getUserRoleName();
			UserRoleEntity role = userRoleRepository.findTop1BByUserRoleName(roleName);
			entity.getUserRole().setId(role.getId());
			entity.getUserRole().setUserRoleCode(role.getUserRoleCode());
		}
		
		userDomainService.userRoleUpdate(entity);
		
		return param.getUserId();
	}
	
	//삭제
	public Long deleteUser(UserDto param) {		
		UserEntity entity = UserDtoMapper.INSTANCE.toEntity(param);
		userDomainService.deleteUser(entity);
		
		try {
			//로그아웃 처리
//			accessService.doLogout(entity.getUserId());
			
			//keycloak 유저 비활성화
//			keyCloakApiUtil.enableSsoUser(param.getUserId(), false);
			
			//keyCloakApiUtil.deleteSsoUser(param);
		} catch (Exception e) {
			log.error(e.getMessage());
		}
		
		
		return 0L;
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
		String contents = genResetPasswordContents(email, requestCode);
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
	
	/**
	 * 유저 활성화 / 비활성화
	 * @param param
	 * @param loginUser
	 * @return
	 */
	public UserDto enableUser(EnableUserDto param, UserDto loginUser) {
		String userId = param.getUserId();
		boolean enable = param.isEnable();
		
		try {			
			String useYn = enable? "Y" : "N";
			
			//DB 활성화
			UserEntity user = userDomainService.getUserInfoByUserId(userId);
			user.setUpdateUserName(loginUser.getUserName());
			user.setUpdateUserId(loginUser.getUserId());
			user.setUpdatedAt(LocalDateTime.now());
			user.setUseYn(useYn);
			
			userDomainService.updateUser(user);
			UserDto userDto = UserDtoMapper.INSTANCE.toDto(user);
			return userDto;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
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
	public Page<UserDto> getAllUserList(Pageable pageable, UserDto.SearchParam param, UserDto loginUser) throws Exception{
		Page<UserEntity> userEntityList = userDomainService.getAllUserList(pageable, param, loginUser);
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
	public void patchUserPassword(String userId, String password) {
		
		/*
		try {
			keyCloakApiUtil.updatePasswordSsoUser(userId, password);
		}catch (Exception e) {
			log.error(e.getMessage());
		}
		*/
		
	}
	
	
	public ResetRequestResult requestResetPassword(String email) {
		ResetRequestResult result = new ResetRequestResult();
		UserEntity entity = userDomainService.getUserInfoByEmailNullable(email);
		if(entity != null) {
			if(entity.getUseYn().equals("Y")) {
				String userId = entity.getUserId();
				requestResetPassword(userId, email);
				
				result.setResult("success");
				result.setUserId(userId);
			} else {
				result.setResult("fail");
				result.setReason("disable user");
			}
			
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
					
					
					boolean enableOk = false;
					boolean changeOk = false;
					try {
						/*
						//계정 활성화.
						enableOk = keyCloakApiUtil.enableSsoUser(user.getUserId(), true);
						log.info("계정 활성화 - userId: {}", id);
						log.info("계정 활성화 - result: {}", enableOk);
						
						changeOk = keyCloakApiUtil.updatePasswordSsoUser(id, password);
						log.info("패스워드 변경 - userId: {}", id);
						log.info("패스워드 변경 - result: {}", changeOk);
						*/
					} catch (Exception e) {
						log.error("", e);
					}
					
					if(enableOk && changeOk) {
						//사용자 사용 설정: use_yn: Y로 설정
						userDomainService.enableUser(id, "Y");
						result = "success";
						
						//패스워드 변경 요청 코드 삭제.
						userDomainService.deleteResetPasswordRequest(id);
					} else {
						result = "fail";
					}
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
	private String genResetPasswordContents(String toEmail, String requestCode) {
		String url = frontUrl;
		if(!url.endsWith("/")) {
			url += "/";
		}		
		url += "#/change-password?requestCode="+requestCode;
		
		
		String icon = frontUrl;
		if(!icon.endsWith("/")) {
			icon += "/";
		}		
		icon += "favicon_strato.png";
		
		String contents = INIT_PASSWORD_TEMPLATE.replace("{ICON}", icon).replace("{E-MAIL}", toEmail).replace("{LINK}", url);
		return contents;
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
	
	/**
	 * 유저 정보 업데이트
	 * @param changeUserDto
	 * @param loginUser
	 * @return
	 */
	public UserDto updateUser(UserDto.ChangeUserDto changeUserDto, UserDto loginUser) {
		String userId = changeUserDto.getUserId();
		UserEntity entity = userDomainService.getUserInfoByUserId(changeUserDto.getUserId());
		if(entity != null) {
			
			String password = changeUserDto.getUserPassword();
			if(password != null && password.length() > 0) {
				//패스워드 변경
				patchUserPassword(changeUserDto.getUserId(), password);
			}
			
			
			//디비 정보 변경.
			entity.setUserName(changeUserDto.getUserName());
			entity.setContact(changeUserDto.getContact());
			entity.setOrganization(changeUserDto.getOrganization());
			entity.setUpdateUserId(loginUser.getUserId());
			entity.setUpdateUserName(loginUser.getUserName());			
			userDomainService.saveUser(entity, "patch");
			
			return UserDtoMapper.INSTANCE.toDto(entity);
		}
		log.error("유저 정보 수정 실패! 유저가 존재하지 않습니다. UserId: {}", userId);
		return null;
	}
	
	/**
	 * 사용자의 메뉴 접근 현황
	 * @param userId
	 * @return
	 */
	public List<UserDto.UserMenuDto> getUserMenu(String userId) {
		List<UserDto.UserMenuDto> userMenu = userDomainService.getUserMenu(userId);
		
		return userMenu;
	}
	
	/**
	 * 유저 존재 유무 리턴.
	 * @param userId
	 * @return
	 */
	public boolean isExistUser(String userId) {
		return userDomainService.isExistUser(userId);
	}
}
