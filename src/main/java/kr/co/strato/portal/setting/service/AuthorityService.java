package kr.co.strato.portal.setting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.portal.setting.model.AuthorityDto;

@Service
public class AuthorityService {
	
	@Autowired
	private UserRoleDomainService userRoleService;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	// 권한 전체 조회 (for front-end)
	public Page<AuthorityDto> getListAuthorityDto(AuthorityDto param, Pageable pageable) {
		return userRoleRepository.getListUserRoleToDto(param, pageable);
	}
	
	// 권한 상세 조회 (for front-end)
	public UserRoleEntity getUserRole() {
		return null;
	}
}
