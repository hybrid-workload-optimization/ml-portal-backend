package kr.co.strato.domain.user.service;


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
	
	public void saveUserRole(UserRoleEntity entity) {
		userRoleRepository.save(entity);
	}
}
