package kr.co.strato.domain.user.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.portal.setting.model.UserRoleDto;

@Service
public class UserRoleDomainService {
	@Autowired
	private UserRoleRepository userRoleRepository;

	public List<UserRoleDto> getListUserRoleDto(UserRoleDto params) {
		return userRoleRepository.getListUserRoleToDto(params);
	}
}
