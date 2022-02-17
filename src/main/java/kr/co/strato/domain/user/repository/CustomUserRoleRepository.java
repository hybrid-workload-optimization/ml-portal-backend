package kr.co.strato.domain.user.repository;

import java.util.List;

import kr.co.strato.portal.setting.model.UserRoleDto;


public interface CustomUserRoleRepository {

	List<UserRoleDto> getListUserRoleToDto(UserRoleDto params);

}
