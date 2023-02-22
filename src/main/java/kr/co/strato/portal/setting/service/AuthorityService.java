package kr.co.strato.portal.setting.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.service.MenuDomainService;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.service.ProjectUserDomainService;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.domain.user.repository.UserRoleRepository;
import kr.co.strato.domain.user.service.UserDomainService;
import kr.co.strato.domain.user.service.UserRoleDomainService;
import kr.co.strato.domain.user.service.UserRoleMenuDomainService;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import kr.co.strato.global.util.KeyCloakApiUtil;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;
import kr.co.strato.portal.setting.model.AuthorityRequestDtoMapper;
import kr.co.strato.portal.setting.model.AuthorityViewDto;
import kr.co.strato.portal.setting.model.AuthorityViewDto.Menu;
import kr.co.strato.portal.setting.model.AuthorityViewDtoMapper;
import kr.co.strato.portal.setting.model.UserAuthorityDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
	
	@Autowired
	private ProjectUserDomainService projectUserDomainService;
	
	@Autowired
	KeyCloakApiUtil keyCloakApiUtil;
	
	@Autowired
	private UserRoleMenuDomainService userRoleMenuDomainService;
	
	
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
			
			//Keycloak Role 생성
			boolean isOk = keyCloakApiUtil.postRole(paramEntity);
			if(!isOk) {
				log.error("Keycloak Role 생성 실패!");
				log.error("Keycloak Role 생성 실패! - Role Code: {}", paramEntity.getUserRoleCode());
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
		UserRoleEntity defaultUserRole = getDefaultUserRole(); // 기본권한 조회
		if ( ObjectUtils.isEmpty(defaultUserRole) ) {
			throw new NotFoundResourceException("user role code : PROJECT_MEMBER");
		}
		
		userRole.getUsers().stream().forEach(user -> {
			user.setUserRole(defaultUserRole);
		});
		userRoleDomainService.saveUserRole(userRole);
		userRoleDomainService.deleteUserRole(userRole);
		
		//Keycloak Role 삭제
		boolean isOk = keyCloakApiUtil.deleteRole(userRole.getUserRoleCode());
		if(!isOk) {
			log.error("Keycloak Role 삭제 실패!");
			log.error("Keycloak Role 삭제 실패! - Role Code: {}", userRole.getUserRoleCode());
		}
		return userRole.getId();
	}

	//권한 수정
	@Transactional
	public Long modifyUserRole(AuthorityRequestDto.ReqModifyDto param) {
		
		//flatmap으로 변경
		if ( !CollectionUtils.isEmpty(param.getMenuList()) ) {
			List<AuthorityRequestDto.Menu> menuList = convertToFlatList(param.getMenuList());
			if ( !CollectionUtils.isEmpty(menuList) ) {
				// flatmap 처리하면서 남은 subMenuList 비우기 & role에 따른 viewableYn, writeableYn 세팅
				for (AuthorityRequestDto.Menu menu : menuList) {
					menu.setSubMenuList(null);
					if ( StringUtils.equals(menu.getRole(), "edit") ) {
						menu.setWritableYn("Y");
						menu.setViewableYn("N");
					} else if ( StringUtils.equals(menu.getRole(), "view") ) {
						menu.setWritableYn("N");
						menu.setViewableYn("Y");
					} else {
						menu.setWritableYn("N");
						menu.setViewableYn("N");
					}
				}
				param.setMenuList(menuList);
			}
		}
		
		UserRoleEntity userRole = userRoleDomainService.getUserRoleById(param.getUserRoleIdx());
		UserRoleEntity defaultUserRole = userRoleDomainService.getUserRoleByCode("PROJECT_MEMBER"); // 기본권한 조회
		if ( ObjectUtils.isEmpty(defaultUserRole) ) {
			throw new NotFoundResourceException("user role code : PROJECT_MEMBER");
		}
		
		// 권한 명 변경
		if ( StringUtils.isNotEmpty(param.getUserRoleName()) ) {
			userRole.setUserRoleName(param.getUserRoleName());
		}
		
		// 권한 설명 변경
		if ( StringUtils.isNotEmpty(param.getDescription()) ) {
			userRole.setDescription(param.getDescription());
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
				
				if(userParam.getType() != null) {
					if ( StringUtils.equals(userParam.getType(), "N") ) {
						//신규 -> 기존 권한 변경 됨 
						UserEntity newUserEntity =  userDomainService.getUserInfoByUserId(userParam.getUserId());
						newUserEntity.setUserRole(userRole);
						
						try {
							keyCloakApiUtil.postUserRole(newUserEntity.getUserId(), userRole.getUserRoleCode());
						} catch (Exception e) {
							log.error("Keycloak Role 변경 오류 - UserId: {}", newUserEntity.getUserId());
							log.error("", e);
							e.printStackTrace();
						}
					} else if (  StringUtils.equals(userParam.getType(), "D")  ) {
						// 권한별 사용자 매핑 변경
						userRole.getUsers().stream().forEach(user -> {
							if ( StringUtils.equals(userParam.getUserId(), user.getUserId()) ) {
								//권한 삭제(초기화)
								user.setUserRole(defaultUserRole);
								try {
									keyCloakApiUtil.postUserRole(user.getUserId(), defaultUserRole.getUserRoleCode());
								} catch (Exception e) {
									log.error("Keycloak Role 변경 오류 - UserId: {}", user.getUserId());
									log.error("", e);
								}
							}
						});
					}
					
					// 권한 변경 유저 로그아웃 처리.
					try {
						keyCloakApiUtil.logoutUser(userParam.getUserId());
						log.info("Logout due to authority change. UserID: {}", userParam.getUserId());
						
					} catch (Exception e) {
						log.error("", e);
					}
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
		Iterator<AuthorityViewDto.Menu> iter =  menuList.iterator();
		while(iter.hasNext()) {
			AuthorityViewDto.Menu menu = iter.next();
			String useYn = menu.getUseYn();
			if(useYn == null || useYn.length() == 0) {
				useYn = "N";
			}
			
			if(useYn.toUpperCase().equals("N")) {
				iter.remove();
			} else {
				allIdMap.put(menu.getMenuIdx(), menu.getMenuIdx());
			}
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
		
		parentList.sort(new Comparator<AuthorityViewDto.Menu>() {

			@Override
			public int compare(Menu o1, Menu o2) {
				int menuOrder1 = o1.getMenuOrder();
				int menuOrder2 = o2.getMenuOrder();
				
				if (menuOrder1 < menuOrder2) {
		            return -1;
		        } else if (menuOrder2 > menuOrder1) {
		            return 1;
		        }
		        return 0;
			}
		});
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
	
	/**
	 * 트리 구조의 메뉴 목록을 flat list로 변경
	 * @param menuList
	 * @return
	 */
	private List<AuthorityRequestDto.Menu> convertToFlatList(List<AuthorityRequestDto.Menu> menuList) {
		return menuList.stream().flatMap(menu -> {
				if ( Objects.nonNull(menu.getSubMenuList()) ) {
					return Stream.concat(Stream.of(menu), convertToFlatList(menu.getSubMenuList()).stream());
				}
				return Stream.of(menu);
			}).collect(Collectors.toList());
	}
	
	/**
	 * 기본 사용자 권한 반환.
	 * @return
	 */
	private UserRoleEntity getDefaultUserRole() {
		return userRoleDomainService.getUserRoleByCode("PROJECT_MEMBER"); // 기본권한 조회
	}
	
	
	
	/**
	 * 사용자별 디폴트 권한 + 프로젝트 권한 반환.
	 * @param userEntity
	 * @return
	 */
	public UserAuthorityDto getUserRole(String userId) {	
		UserEntity userEntity = userDomainService.getUserInfoByUserId(userId);
		
		UserAuthorityDto userAuthroityDto = new UserAuthorityDto();
		
		//디폴트 권한 설정.
		
		UserRoleEntity userRole = userEntity.getUserRole();
		if(userRole == null) {
			//권한 설정 되어있지 않은 경우 디폴트 권한 설정
			userRole = getDefaultUserRole();
		}
		
		AuthorityViewDto defaultView = AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDto(userRole);
		List<AuthorityViewDto.Menu> defaultMenu = getMenuTreeList(defaultView.getMenuList());
		
		userAuthroityDto.setDefaultUserRole(defaultMenu);
		
		//프로젝트 별 권한 설정
		Map<Long, List<AuthorityViewDto.Menu>> projectUserRole = new HashMap<>();
		userAuthroityDto.setProjectUserRole(projectUserRole);
		
		
		List<ProjectUserEntity> projectUsers = projectUserDomainService.findByUserId(userId);
		for(ProjectUserEntity pu : projectUsers) {
			Long projectIdx = pu.getProjectIdx();
			
			//TODO 사용자 권한 바뀌면 설정
			Long userRoleIdx = pu.getUserRoleIdx();
			
			UserRoleEntity roleEntity = userRoleDomainService.getUserRoleById(userRoleIdx);
			if(roleEntity != null) {
				AuthorityViewDto projectView = AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDto(roleEntity);
				List<AuthorityViewDto.Menu> projectMenu = getMenuTreeList(projectView.getMenuList());
				
				projectUserRole.put(projectIdx, projectMenu);
			}
		}
		
		
		return userAuthroityDto;
	}
	
	public UserAuthorityDto getUserRole(String userId, String roleCode) {
		UserAuthorityDto userAuthroityDto = new UserAuthorityDto();
		if(roleCode == null) {
			//권한 설정 되어있지 않은 경우 디폴트 권한 설정
			roleCode = "PROJECT_MEMBER";
		}
		
		List<UserRoleMenuEntity> list = userRoleMenuDomainService.getUserRoleMenuList(roleCode);
		List<AuthorityViewDto.Menu> menuDto = list.stream()
				.map(m -> AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDtoInnerMenu(m))
				.collect(Collectors.toList());			
				
		List<AuthorityViewDto.Menu> defaultMenu = getMenuTreeList(menuDto);
		userAuthroityDto.setDefaultUserRole(defaultMenu);
		
		//프로젝트 별 권한 설정
		Map<Long, List<AuthorityViewDto.Menu>> projectUserRole = new HashMap<>();
		userAuthroityDto.setProjectUserRole(projectUserRole);
		
		
		List<ProjectUserEntity> projectUsers = projectUserDomainService.findByUserId(userId);
		for(ProjectUserEntity pu : projectUsers) {
			Long projectIdx = pu.getProjectIdx();
			
			//TODO 사용자 권한 바뀌면 설정
			Long userRoleIdx = pu.getUserRoleIdx();
			
			if(userRoleIdx != null) {
				List<UserRoleMenuEntity> plist = userRoleMenuDomainService.getUserRoleMenuList(userRoleIdx);
				List<AuthorityViewDto.Menu> pMenuDto = plist.stream()
						.map(m -> AuthorityViewDtoMapper.INSTANCE.toAuthorityViewDtoInnerMenu(m))
						.collect(Collectors.toList());			
						
				List<AuthorityViewDto.Menu> pDefaultMenu = getMenuTreeList(pMenuDto);
				projectUserRole.put(projectIdx, pDefaultMenu);
			}
		}
		return userAuthroityDto;
	}
}
