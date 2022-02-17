package kr.co.strato.portal.setting.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.portal.setting.model.UserRoleDto;

@Service
public class UserRoleService {
	
	@Autowired
	private UserRoleDomainService userRoleService;
	
	// 권한 전체 조회 (for front-end)
	public List<UserRoleDto> getListUserRoleDto() {
		return userRoleService.getListUserRoleDto(null);
	}
	
	// 권한 상세 조회 (for front-end)
	public UserRoleEntity getUserRole() {
		return null;
	}
}
