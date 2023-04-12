package kr.co.strato.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.apache.ibatis.javassist.NotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.adapter.sso.service.PortalAdapterService;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserResetPasswordEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.repository.UserRepository;
import kr.co.strato.domain.user.repository.UserResetPasswordRepository;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;
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
	ProjectUserRepository projectUserRepository;
	
	@Autowired
	UserResetPasswordRepository resetPasswordRepostory;
	
	@Autowired
	PortalAdapterService portalAdapterService;
	
	
	/**
	 * 유저 등록/수정
	 * @param entity
	 */
	public void saveUser(UserEntity entity, String mode) {
		
		// 등록
		if("post".equals(mode)) {
			//DB 저장
			
			userRepository.save(entity);
			
		} else {
			
			// 수정할 유저의 roleCode > 변경했다면 코드값이 있고, 없다면 값이 없음
			String roleCode = "";
			if(entity.getUserRole() != null) {
				roleCode = entity.getUserRole().getUserRoleCode();
			}

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
				if(entity.getEmail() != null && !"".equals(entity.getEmail())) {
					pUser.get().setEmail(entity.getEmail());
				}
				if(entity.getUseYn() != null && !"".equals(entity.getUseYn())) {
					pUser.get().setUseYn(entity.getUseYn());
				}
				if(entity.getUpdateUserName() != null && !"".equals(entity.getUpdateUserName())) {
					pUser.get().setUpdateUserName(entity.getUpdateUserName());
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
	 * 유저 롤 추가/삭제
	 * @param entity
	 */
	public void userRoleUpdate(UserEntity entity) {
		
		// 수정할 유저의 roleCode > 변경했다면 코드값이 있고, 없다면 값이 없음
		String roleCode = "";
		if(entity.getUserRole() != null) {
			roleCode = entity.getUserRole().getUserRoleCode();
		}

		// 수정
		Optional<UserEntity> pUser = userRepository.findByUserId(entity.getUserId());
		if(pUser.isPresent()) {
			if(entity.getUserRole() != null) {
				UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);
				pUser.get().setUserRole(role);
				pUser.get().setUseYn("Y");
			} else {
				pUser.get().setUserRole(null);
				pUser.get().setUseYn("N");
			}
			if(entity.getUpdateUserName() != null && !"".equals(entity.getUpdateUserName())) {
				pUser.get().setUpdateUserName(entity.getUpdateUserName());
			}
			userRepository.save(pUser.get());

		}else {
			throw new NotFoundResourceException("user : " + entity.toString());
		}
		
	}
	
	
	
	/**
	 * 유저 삭제
	 * @param user
	 * @return
	 */
	@Transactional
	public String deleteUser(UserEntity user) {
		
		//@TODO 유저 삭제에 대한 정책 확정 필요
		Optional<UserEntity> entity =  userRepository.findById(user.getUserId());
		
		if(entity.isPresent()) {
			String userId = user.getUserId();
			List<ProjectUserEntity> projectUsers = entity.get().getProjectUser();
			
			for(ProjectUserEntity projectUser : projectUsers) {
				Long projectUserIdx = projectUser.getProjectIdx();
				projectUserRepository.deleteByProjectIdxAndUserId(projectUserIdx, userId);
			}
			
			entity.get().setUseYn("N");
			userRepository.save(entity.get());
		}
		
//		userRepository.deleteByUserId(userId);
		
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
	
	/**
	 * 유저가 존재하는지 조회
	 * @param userId
	 * @return
	 */
	public boolean isExistUser(String userId) {
		Optional<UserEntity> entity =  userRepository.findById(userId);
		return entity.isPresent();
	}
}
