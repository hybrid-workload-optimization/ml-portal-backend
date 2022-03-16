package kr.co.strato.portal.setting.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.auth.AUTH;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.service.MenuDomainService;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
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
	
	// 권한/그룹 중복체크(명칭)
	public Boolean getUserRoleDuplicateCheck(String userRoleName, String groupYn) {
		int i = userRoleDomainService.getUserRoleDuplicateCheck(userRoleName, groupYn);
		if ( i > 0 ) {
			return true;
		}else {
			return false;
		}
	}
	
	// 권한 전체 조회 (for front-end)
	public List<AuthorityViewDto> getAllListAuthorityToDto() {
		List<UserRoleEntity> userRoleList = userRoleDomainService.getAllListAuthority();
		List<AuthorityViewDto> authorityList = new ArrayList<AuthorityViewDto>();
		
		if ( !CollectionUtils.isEmpty(userRoleList) ) {
			authorityList = AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDtoList(userRoleList);
		}else {
			return new ArrayList<>();
		}
		
		if ( !CollectionUtils.isEmpty(authorityList) ) {
			for ( AuthorityViewDto auth : authorityList ) {
				if ( !CollectionUtils.isEmpty(auth.getMenuList()) ) {
					for ( AuthorityViewDto.Menu menu : auth.getMenuList() ) {
						if ( StringUtils.equals("Y", menu.getViewableYn()) ) {
							menu.setRole("view");
						} else if ( StringUtils.equals("Y", menu.getWritableYn()) ) {
							menu.setRole("edit");
						} else {
							menu.setRole("none");
						}
					}
				}
			}
			
			//트리형태로 변환
			return getAuthorityTreeList(authorityList);
		}else {
			return new ArrayList<>();
		}
	}
	
	// 권한 상세 조회 (for front-end)
	public AuthorityViewDto getAuthorityToDto(Long authId) {
		UserRoleEntity userRole = userRoleDomainService.getUserRoleById(authId);
		AuthorityViewDto authority = AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDto(userRole);
		
		if ( ObjectUtils.isNotEmpty(authority) && authority.getMenuList() != null && authority.getMenuList().size() > 0 ) {
			List<AuthorityViewDto.Menu> treeMenuList = getMenuTreeList(authority.getMenuList());
			authority.setMenuList(treeMenuList);
		}
		
		return authority;
	}
	
	// 권한 신규 저장
	@Transactional
	public Long postUserRole(AuthorityRequestDto.ReqRegistDto param) {
		UserRoleEntity paramEntity = AuthorityRequestDtoMapper.INSTANCE.toEntity(param);
		
		if ( StringUtils.equals(param.getGroupYn(), "Y") ) {
			//권한그룹 생성
			paramEntity.setUserRoleCode("GROUP_" + Long.toString(System.currentTimeMillis()));
			paramEntity.setParentUserRoleIdx((long)0);
		} else {
			paramEntity.setUserRoleCode("ROLE_" + Long.toString(System.currentTimeMillis()));
			
			//자식일경우 그룹에 매핑된 실제 권한이므로 메뉴생성
			List<MenuEntity> menus = menuDomainService.getAllMenu();
			for ( MenuEntity menu : menus ) {
				UserRoleMenuEntity userRoleMenu = new UserRoleMenuEntity();
				userRoleMenu.setMenu(menu);
				userRoleMenu.setViewableYn("N");
				userRoleMenu.setWritableYn("N");
				userRoleMenu.setCreated_at(new Date());
				paramEntity.addToUserRoleMenu(userRoleMenu);
			}
		}
		paramEntity.setUserDefinedYn("Y");
		
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
		if ( !CollectionUtils.isEmpty(param.getUserList()) ) {
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
		}
		
		userRoleDomainService.saveUserRole(userRole);
		
		return userRole.getId();
	}
	
	private List<AuthorityViewDto> getAuthorityTreeList(List<AuthorityViewDto> roleList) {
		List<AuthorityViewDto> treeList     = new ArrayList<>();
		List<AuthorityViewDto> childrenList = new ArrayList<>();

		if ( roleList != null && roleList.size() > 0 ) {
			for ( AuthorityViewDto role : roleList ) {
				if ( StringUtils.equals("Y", role.getGroupYn()) ) {
					treeList.add(role);
				}
				else {
					childrenList.add(role);
				}
			}

			for ( AuthorityViewDto pTree : treeList ) {
				List<AuthorityViewDto> cTreeList = new ArrayList<>();
				List<AuthorityViewDto.Menu> treeMenuList = new ArrayList<>(); //메뉴구조 트리화를 위함
				for ( AuthorityViewDto cTree : childrenList ) {
					if ( StringUtils.equals(pTree.getUserRoleIdx().toString(), cTree.getParentUserRoleIdx().toString()) ) {
						// 서브롤이 확인됬을경우
						treeMenuList = getMenuTreeList(cTree.getMenuList());
						
						// 메뉴 > 트리화
						cTree.setMenuList(treeMenuList);
						
						// 트리 리스트에 세팅
						cTreeList.add(cTree);
					}
				}
				pTree.setSubRoleList(cTreeList);
			}
		}

		return treeList;
	}
	
	private List<AuthorityViewDto.Menu> getMenuTreeList(List<AuthorityViewDto.Menu> menuList) {
		List<AuthorityViewDto.Menu> parentList   = new ArrayList<>();
		List<AuthorityViewDto.Menu> childrenList = new ArrayList<>();

		Map<Long, Long> allIdMap = new HashMap<>();
		for ( AuthorityViewDto.Menu menu : menuList ) {
			allIdMap.put(menu.getMenuIdx(), menu.getMenuIdx());
		}

		for ( AuthorityViewDto.Menu menu : menuList ) {
			if ( StringUtils.equals(menu.getParentMenuIdx().toString(), "0") || !allIdMap.containsKey(menu.getParentMenuIdx()) ) {
				parentList.add(menu);
			}
			else {
				childrenList.add(menu);
			}
		}

		if ( !CollectionUtils.isEmpty(parentList) ) {
			for ( AuthorityViewDto.Menu menu : parentList ) {
				List<AuthorityViewDto.Menu> children = getChildrenList(childrenList, menu.getMenuIdx());
				if ( !CollectionUtils.isEmpty(children) ) {
					menu.setSubMenuList(children);
				}
			}
		}

		return parentList;
	}
	
	private List<AuthorityViewDto.Menu> getChildrenList(List<AuthorityViewDto.Menu> menuList, Long superMenuId) {
		List<AuthorityViewDto.Menu> parentList   = new ArrayList<>();
		List<AuthorityViewDto.Menu> childrenList = new ArrayList<>();
		if ( menuList == null || menuList.size() == 0 ) {
			return parentList;
		}

		for ( AuthorityViewDto.Menu menu : menuList ) {
			if ( StringUtils.equals(superMenuId.toString(), menu.getParentMenuIdx().toString()) ) {
				parentList.add(menu);
			}
			else {
				childrenList.add(menu);
			}
		}

		if ( !CollectionUtils.isEmpty(parentList) ) {
			for ( AuthorityViewDto.Menu menu : parentList ) {
				List<AuthorityViewDto.Menu> children = getChildrenList(childrenList, menu.getMenuIdx());
				if ( !CollectionUtils.isEmpty(children) ) {
					menu.setSubMenuList(children);
				}
			}
		}

		return parentList;
	}
}
