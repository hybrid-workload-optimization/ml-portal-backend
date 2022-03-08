package kr.co.strato.portal.setting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.service.MenuDomainService;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.global.error.exception.AlreadyExistResourceException;
import kr.co.strato.global.error.exception.NotFoundResourceException;
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
	
	@Autowired
	private UserDomainService userDomainService;
	
	
	public Page<AuthorityRequestDto> getListPagingAuthorityDto(AuthorityRequestDto.ReqViewDto param, Pageable pageable) {
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
	public Long postUserRole(AuthorityRequestDto.ReqRegistDto param) {
		UserRoleEntity paramEntity = AuthorityRequestDtoMapper.INSTANCE.toEntity(param);
		UserRoleEntity userRole = userRoleDomainService.getUserRoleByCode(paramEntity.getUserRoleName());
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
	
	// 권한 삭제
	@Transactional
	public Long deleteUserRole(AuthorityRequestDto.ReqDeleteDto param) {
		UserRoleEntity userRole = userRoleDomainService.getUserRoleById(param.getUserRoleIdx());
		UserRoleEntity defaultUserRole = userRoleDomainService.getUserRoleByCode("PROJECT_MEMBER"); // 기본권한 조회
		if ( ObjectUtils.isEmpty(defaultUserRole) ) {
			throw new NotFoundResourceException("user role code : PROJECT_MEMBER");
		}
		
		userRole.getUsers().stream().forEach(user -> {
			user.setUserRole(defaultUserRole);
		});
		userRoleDomainService.saveUserRole(userRole);
		userRoleDomainService.deleteUserRole(userRole);
		
		return userRole.getId();
	}

	//권한 수정
	@Transactional
	public Long modifyUserRole(AuthorityRequestDto.ReqModifyDto param) {
		UserRoleEntity userRole = userRoleDomainService.getUserRoleById(param.getUserRoleIdx());
		UserRoleEntity defaultUserRole = userRoleDomainService.getUserRoleByCode("PROJECT_MEMBER"); // 기본권한 조회
		if ( ObjectUtils.isEmpty(defaultUserRole) ) {
			throw new NotFoundResourceException("user role code : PROJECT_MEMBER");
		}
		
		// 권한 명 변경
		if ( StringUtils.isNotEmpty(param.getUserRoleName()) ) {
			userRole.setUserRoleName(param.getUserRoleName());
		}
		
		// 권한별 메뉴 사용여부 변경
		userRole.getUserRoleMenus().stream().forEach(roleMenu -> {
			param.getMenuList().stream().forEach(menuParam -> {
				if ( roleMenu.getMenu().getMenuIdx().equals(menuParam.getMenuIdx()) ) {
					roleMenu.setViewableYn(menuParam.getViewableYn());
					roleMenu.setWritableYn(menuParam.getWritableYn());
				}
			});
		});
		
		// 신규 사용자 매핑
		param.getUserList().stream().forEach(userParam -> {
			if ( StringUtils.equals(userParam.getType(), "N") ) {
				//신규
				UserEntity newUserEntity =  userDomainService.getUserInfoByUserId(userParam.getUserId());
				newUserEntity.setUserRole(userRole);
			}else if (  StringUtils.equals(userParam.getType(), "D")  ) {
				// 권한별 사용자 매핑 변경
				userRole.getUsers().stream().forEach(user -> {
					if ( StringUtils.equals(userParam.getUserId(), user.getUserId()) ) {
						//권한 삭제(초기화)
						user.setUserRole(defaultUserRole);
					}
				});
			}
		});
		userRoleDomainService.saveUserRole(userRole);
		
		return userRole.getId();
	}
}
