package kr.co.strato.domain.user.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.domain.user.repository.UserRoleMenuRepository;
import kr.co.strato.domain.user.repository.UserRoleRepository;

@Service
public class UserRoleMenuDomainService {
	
	@Autowired
	private UserRoleMenuRepository userRoleMenuRepository;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	public List<UserRoleMenuEntity> getUserRoleMenuList(String userRoleCode) {
		
		UserRoleEntity userRole = userRoleRepository.findTop1BByUserRoleCode(userRoleCode);
		if(userRole != null) {
			return userRoleMenuRepository.findByUserRole(userRole);
		}
		return null;
	}
	
	public List<UserRoleMenuEntity> getUserRoleMenuList(Long userRoleIdx) {
		Optional<UserRoleEntity> opt = userRoleRepository.findById(userRoleIdx);
		if(opt.isPresent()) {
			return userRoleMenuRepository.findByUserRole(opt.get());
		}
		return null;
	}
}
