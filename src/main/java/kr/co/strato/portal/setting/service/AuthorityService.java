package kr.co.strato.portal.setting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.service.MenuDomainService;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.error.exception.AlreadyExistResourceException;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDtoMapper;
import kr.co.strato.portal.setting.model.AuthorityViewDto;
import kr.co.strato.portal.setting.model.AuthorityViewDtoMapper;

@Service
public class AuthorityService {
	
	@Autowired
	private UserRoleDomainService userRoleDomainService;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private MenuDomainService menuDomainService;
	
	
	public Page<AuthorityRequestDto> getListPagingAuthorityDto(AuthorityRequestDto param, Pageable pageable) {
		return userRoleRepository.getListPagingUserRoleToDto(param, pageable);
	}
	
	// 권한 전체 조회 (for front-end)
	public List<AuthorityViewDto> getAllListAuthorityToDto() {
		List<UserRoleEntity> userRoleList = userRoleDomainService.getAllListAuthority();
		List<AuthorityViewDto> authorityList = new ArrayList<>();
		
		if ( userRoleList.size() > 0 ) {
			authorityList = AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDtoList(userRoleList);
		}
		
		return authorityList;
	}
	
	// 권한 상세 조회 (for front-end)
	public AuthorityViewDto getAuthorityToDto(Long authId) {
		UserRoleEntity userRole = userRoleDomainService.getUserRoleById(authId);
		AuthorityViewDto authority = AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDto(userRole);
		
		return authority;
	}
	
	// 권한 신규 저장
	@Transactional
	public Long postUserRole(AuthorityRequestDto param) {
		UserRoleEntity paramEntity = AuthorityRequestDtoMapper.INSTANCE.toEntity(param);
		UserRoleEntity userRole = userRoleDomainService.getUserRoleByName(paramEntity.getUserRoleName());
		if ( ObjectUtils.isNotEmpty(userRole) ) {
			throw new AlreadyExistResourceException("role name : " + param.getUserRoleName());
		}
		
		List<MenuEntity> menus = menuDomainService.getAllMenu();
		for ( MenuEntity menu : menus ) {
			UserRoleMenuEntity userRoleMenu = new UserRoleMenuEntity();
			userRoleMenu.setMenu(menu);
			userRoleMenu.setViewableYn("N");
			userRoleMenu.setWritableYn("N");
			userRoleMenu.setCreated_at(new Date());
			paramEntity.addToUserRoleMenu(userRoleMenu);
		}
		userRoleDomainService.saveUserRole(paramEntity);
		
		return paramEntity.getId();
	}
	
	@Transactional
	public Long deleteUserRole(AuthorityRequestDto param) {
		UserRoleEntity userRole = userRoleDomainService.getUserRoleById(param.getUserRoleIdx());
		userRoleDomainService.deleteUserRole(userRole);
		return userRole.getId();
	}
}
