package kr.co.strato.domain.user.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class UserRoleDomainService {
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	public UserRoleEntity getUserRoleById(Long id) {
		UserRoleEntity userRole = userRoleRepository.findById(id)
				.orElseThrow(() -> new NotFoundResourceException("userRole id:"+id));
		
		return userRole;
	}
	
	public UserRoleEntity getUserRoleByName(String name) {
		UserRoleEntity userRole = userRoleRepository.findTop1BByUserRoleName(name);
		return userRole;
	}
	
	public UserRoleEntity getUserRoleByCode(String code) {
		UserRoleEntity userRole = userRoleRepository.findTop1BByUserRoleCode(code);
		return userRole;
	}
	
	public void saveUserRole(UserRoleEntity entity) {
		userRoleRepository.save(entity);
	}
	
	public void deleteUserRole(UserRoleEntity entity) {
		userRoleRepository.delete(entity);
	}

	public List<UserRoleEntity> getAllListAuthority() {	
		return userRoleRepository.findByUserRoleCodeNot(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN);
	}
	
	/*
	 * 사용 가능한 유저 권한 목록 반환.
	 */
	public List<UserRoleEntity> getUseUserRole() {
		return userRoleRepository.findByUserRole(2L, "N");
	}
	
	public List<UserRoleEntity> getProjectUserRole() {
//		UserRoleEntity projectGroup = getUserRoleByCode("PROJECT");
		UserRoleEntity projectGroup = getUserRoleByCode("PROJECT_MEMBER");
//		List<UserRoleEntity> list = userRoleRepository.findByParentUserRoleIdx(projectGroup.getId(), UserRoleEntity.ROLE_CODE_PROJECT_MANAGER);
		List<UserRoleEntity> list = userRoleRepository.findByUserRole(projectGroup.getId(), "N");
		return list;
	}

	public int getUserRoleDuplicateCheck(String userRoleName, String groupYn) {
		int count = userRoleRepository.findCountByAccessRoleNameAndGroupYn(userRoleName, groupYn);
		return count;
	}
}
