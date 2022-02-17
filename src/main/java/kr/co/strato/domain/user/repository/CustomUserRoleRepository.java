package kr.co.strato.domain.user.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import kr.co.strato.portal.setting.model.AuthorityDto;


public interface CustomUserRoleRepository {

	Page<AuthorityDto> getListUserRoleToDto(AuthorityDto params, Pageable pageable);

}
