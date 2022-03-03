package kr.co.strato.portal.setting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.repository.MenuRepository;
import kr.co.strato.domain.menu.service.MenuDomainService;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.portal.setting.model.AuthorityDto;
import kr.co.strato.portal.setting.model.AuthorityDtoMapper;

@Service
public class AuthorityService {
	
	@Autowired
	private UserRoleDomainService userRoleDomainService;
	
	@Autowired
	private UserRoleRepository userRoleRepository;
	
	@Autowired
	private MenuDomainService menuDomainService;
	
	// 권한 전체 조회 (for front-end)
	public Page<AuthorityDto> getListAuthorityDto(AuthorityDto param, Pageable pageable) {
		return userRoleRepository.getListUserRoleToDto(param, pageable);
	}
	
	// 권한 상세 조회 (for front-end)
	public UserRoleEntity getUserRole() {
		return null;
	}
	
	// 권한 신규 저장
	@Transactional
	public Long postUserRole(AuthorityDto param) {
		UserRoleEntity paramEntity = AuthorityDtoMapper.INSTANCE.toEntity(param);
		System.out.println("####paramEntity :: " + paramEntity.toString());
		
		UserRoleEntity userRole = userRoleDomainService.getUserRoleByName(paramEntity.getUserRoleName());
		List<UserRoleMenuEntity> userRoleMenus = new ArrayList<UserRoleMenuEntity>(); //권한별 메뉴 담을 용도
		
		if ( !ObjectUtils.isNotEmpty(userRole) ) {
			System.out.println("#### IS EXIST ..CODE EXCEPTION..");
		}
		
		List<MenuEntity> menus = menuDomainService.getAllMenu();
		for ( MenuEntity menu : menus ) {
			UserRoleMenuEntity userRoleMenu = new UserRoleMenuEntity();
			userRoleMenu.setMenu(menu);
			userRoleMenu.setViewableYn("N");
			userRoleMenu.setWritableYn("N");
			userRoleMenu.setCreated_at(new Date());
//			userRoleMenu.setId(paramEntity.getId());
//			userRoleMenu.setUserRole(paramEntity);
//			userRoleMenus.add(userRoleMenu);
			paramEntity.addToUserRoleMenu(userRoleMenu);
		}
//		paramEntity.setUserRoleMenus(userRoleMenus);
		
		userRoleDomainService.saveUserRole(paramEntity);
		
		return paramEntity.getId();
	}
}
