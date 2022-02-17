package kr.co.strato.domain.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.portal.setting.model.AuthorityDto;

@Service
public class UserRoleDomainService {
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	public Page<AuthorityDto> getListUserRoleDto(AuthorityDto params, Pageable pageable) {
		return userRoleRepository.getListUserRoleToDto(params, pageable);
	}
}
