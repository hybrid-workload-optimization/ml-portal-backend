package kr.co.strato.domain.user.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.repository.UserRepository;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.portal.setting.model.UserDto.SearchParam;

/**
 * @author tmdgh
 *
 */
@Service
public class UserDomainService {
	
	@Autowired
	UserRepository userRepository;
	
	@Autowired
	UserRoleRepository userRoleRepository;
	
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
		
			//PROJECT MEMBER의 RoleCode 가져오기
			String roleCode = "PROJECT_MEMBER";
			UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);
			
			//권한 매핑 
			entity.getUserRole().setId(role.getId());
			
			userRepository.save(entity);	
		}else {
			
			// 수정할 유저의 roleCode > 변경했다면 코드값이 있고, 없다면 값이 없음
			String roleCode = entity.getUserRole().getUserRoleCode();

			// 수정
			UserEntity pUser = userRepository.findByUserId(entity.getUserId());
			if(entity.getContact() != null && !"".equals(entity.getContact())) {
				pUser.setContact(entity.getContact());
			}
			if(entity.getOrganization() != null && !"".equals(entity.getOrganization())) {
				pUser.setOrganization(entity.getOrganization());
			}
			if(entity.getUserRole().getUserRoleCode() != null && !"".equals(entity.getUserRole().getUserRoleCode())) {
				UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);
//				pUser.getUserRole().setId(role.getId());
				pUser.setUserRole(role);
				
			}
			System.out.println("==================유저 수정");
			System.out.println(pUser.toString());
			System.out.println("==================유저 수정");
			userRepository.save(pUser);
			
			UserRoleEntity role = userRoleRepository.findTop1BByUserRoleCode(roleCode);
			
			// 기존 ROLE와 업데이트할 ROLE이 다르면, keycloak Update
			if(roleCode != null && !"".equals(roleCode) && !roleCode.equals(role.getUserRoleCode())) {
				// keycloak Role Update 필요
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
	
	public Page<UserEntity> getAllUserList(Pageable pageable, SearchParam param) {
		Page<UserEntity> list =  userRepository.getListUserWithParam(pageable, param);
		return list;
	}

	
	/**
	 * Email로 유저 정보 검색 > 단일
	 * @param email
	 * @return
	 */
	public UserEntity getUserInfoByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	
	/**
	 * UserId로 검색 > 단일
	 * @param userId
	 * @return
	 */
	public UserEntity getUserInfoByUserId(String userId) {
//		return userRepository.findByUserId(userId);
		// 사용중인 user만 검색하기 위해 useYn  추가
		return userRepository.findByUserIdAndUseYn(userId, "Y");
	}
	
	/**
	 * 이름으로 유저 정보 검색 > 다중
	 * @param userName
	 * @return
	 */
	public Page<UserEntity> getUserListByUserName(String userName, Pageable pageable){
		
		return userRepository.findByUserName(userName, pageable);
	}



	
	

}
