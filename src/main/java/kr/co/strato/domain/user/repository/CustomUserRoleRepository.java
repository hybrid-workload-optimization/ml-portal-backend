package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;


public interface CustomUserRoleRepository {

	Page<AuthorityRequestDto> getListPagingUserRoleToDto(AuthorityRequestDto.ReqViewDto params, Pageable pageable);
	
	List<UserRoleEntity> findByUserRole(Long notId, String groupYn);

}
