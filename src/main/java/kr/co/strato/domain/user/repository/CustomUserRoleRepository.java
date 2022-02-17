package kr.co.strato.domain.user.repository;

import java.util.List;

import kr.co.strato.portal.setting.model.AuthorityDto;


public interface CustomUserRoleRepository {

	List<AuthorityDto> getListUserRoleToDto(AuthorityDto params);

}
