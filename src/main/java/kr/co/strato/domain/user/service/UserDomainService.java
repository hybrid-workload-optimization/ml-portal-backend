package kr.co.strato.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserResetPasswordEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.repository.UserRepository;
import kr.co.strato.domain.user.repository.UserResetPasswordRepository;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.SearchParam;
import lombok.extern.slf4j.Slf4j;

/**
 * @author tmdgh
 *
 */
@Service
@Slf4j
public class UserDomainService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
	@Autowired
	UserResetPasswordRepository resetPasswordRepostory;
	
	@Autowired
	KeyCloakApiUtil	keyCloakApiUtil;
	
	/**
	 * 유저 등록/수정
	 * @param entity
	 */
	public void saveUser(UserEntity entity, String mode) {

		// 등록
		if("post".equals(mode)) {
			//DB 저장
			
			if(entity.getUserRole() == null) {
				//PROJECT MEMBER의 RoleCode 가져오기
				String roleCode = "PROJECT_MEMBER";
				UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);
				
				//권한 매핑 
				entity.getUserRole().setId(role.getId());
			}
			userRepository.save(entity);	
		} else {
			
			// 수정할 유저의 roleCode > 변경했다면 코드값이 있고, 없다면 값이 없음
			String roleCode = entity.getUserRole().getUserRoleCode();

			// 수정
			Optional<UserEntity> pUser = userRepository.findByUserId(entity.getUserId());
			if(pUser.isPresent()) {
				if(entity.getUserName() != null && !"".equals(entity.getUserName())) {
					pUser.get().setUserName(entity.getUserName());
				}
				if(entity.getContact() != null && !"".equals(entity.getContact())) {
					pUser.get().setContact(entity.getContact());
				}
				if(entity.getOrganization() != null && !"".equals(entity.getOrganization())) {
					pUser.get().setOrganization(entity.getOrganization());
				}
				if(entity.getUserRole().getUserRoleCode() != null && !"".equals(entity.getUserRole().getUserRoleCode())) {
					UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);
//					pUser.getUserRole().setId(role.getId());
					pUser.get().setUserRole(role);
				}
				userRepository.save(pUser.get());


			}else {
				throw new NotFoundResourceException("user : " + entity.toString());
			}
			
			UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);						
			// 기존 ROLE와 업데이트할 ROLE이 다르면, keycloak Update
			if(roleCode != null && !"".equals(roleCode) && !roleCode.equals(role.getUserRoleCode())) {
				// keycloak Role Update 필요
				/***
				 * 키클락과 포탈의 권한 체계가 다른데 어떻게 하면 좋을지? 
				 * 
				 */
			}
			
		}
	}
	
	
	
	/**
	 * 유저 삭제
	 * @param user
	 * @return
	 */
	public String deleteUser(UserEntity user) {
		
		//@TODO 유저 삭제에 대한 정책 확정 필요
		Optional<UserEntity> entity =  userRepository.findById(user.getUserId());
		
		if(entity.isPresent()) {
			entity.get().setUseYn("N");
			userRepository.save(entity.get());
		}
		
		return user.getUserId();
	}
	
	/**
	 * 모든 유저 정보
	 * @return
	 */
	public Page<UserEntity> getAllUserList(Pageable pageable){
		Page<UserEntity> list =  userRepository.findByUseYn("Y", pageable);
		return list;
	}
	
	public Page<UserEntity> getAllUserList(Pageable pageable, SearchParam param, UserDto loginUser) {
		Page<UserEntity> list =  userRepository.getListUserWithParam(pageable, param, loginUser);
		return list;
	}

	
	/**
	 * Email로 유저 정보 검색 > 단일
	 * @param email
	 * @return
	 */
	public UserEntity getUserInfoByEmail(String email) {
		Optional<UserEntity> user = userRepository.findByEmail(email);
		if(user.isPresent()) {
			return user.get();
		}else {
			throw new NotFoundResourceException("userEmail : " + email);
		}
	}
	
	public UserEntity getUserInfoByEmailNullable(String email) {
		Optional<UserEntity> user = userRepository.findByEmail(email);
		if(user.isPresent()) {
			return user.get();
		}
		return null;
	}
	
	/**
	 * UserId로 검색 > 단일
	 * @param userId
	 * @return
	 * @throws NotFoundException 
	 */
	public UserEntity getUserInfoByUserId(String userId) {		
		Optional<UserEntity> user = userRepository.findByUserId(userId); 
		if(user.isPresent()) {
			return user.get();
		}else {
			throw new NotFoundResourceException("userID : " + userId);
		}
	}
	
	/**
	 * 이름으로 유저 정보 검색 > 다중
	 * @param userName
	 * @return
	 */
	public Page<UserEntity> getUserListByUserName(String userName, Pageable pageable) {
		
		return userRepository.findByUserName(userName, pageable);
	}


	/**
	 * 패스워드 변경 요청 저장.
	 * @param resetEntity
	 */
	public void saveResetPasswordRequest(UserResetPasswordEntity resetEntity) {
		resetPasswordRepostory.save(resetEntity);
	}
	
	/**
	 * 패스워드 변경 요청 반환.
	 * @param requestCode
	 * @return
	 */
	public UserResetPasswordEntity getResetPasswordRequest(String requestCode) {
		return resetPasswordRepostory.findByRequestCode(requestCode);
	}
	
	/**
	 * 패스워드 변경 요청 반환.
	 * @param requestCode
	 * @return
	 */
	public UserResetPasswordEntity getResetPasswordRequestByUserId(String userId) {
		return resetPasswordRepostory.findByUserId(userId);
	}
	
	/**
	 * 패스워드 변경 요청 삭제.
	 * @param email
	 */
	public void deleteResetPasswordRequest(String userId) {
		resetPasswordRepostory.deleteByUserId(userId);
	}
	
	/**
	 * 사용자의 메뉴 접근 현황
	 * @param userId
	 */
	public List<UserDto.UserMenuDto> getUserMenu(String userId) {
		return userRepository.getUserMenu(userId);
	}
	
	/**
	 * 사용자 활성화.
	 * @param userId
	 */
	public void enableUser(String userId, String useYn) {
		userRepository.setUseYnByUser(useYn, userId);
	}
	
	/**
	 * 유저 정보 수정
	 * @param entity
	 */
	public void updateUser(UserEntity entity) {
		userRepository.save(entity);	
	}
}
