package kr.co.strato.portal.common.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.project.service.ProjectUserDomainService;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;
import kr.co.strato.global.error.exception.PermissionDenyException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProjectAuthorityService {
	
	@Autowired
	private ProjectUserDomainService projectUserDomainService;
	
	
	/**
	 * 접근 권한이 있는지 확인하여 권한이 없는경우 11003 에러 발생
	 * @param projectIdx
	 * @param userId
	 * @param menuCode
	 */
	public void chechAuthority(Long menuCode, Long projectIdx, UserDto loginUser) {		
		UserDto.UserRole role = loginUser.getUserRole();
		if(role.getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN) 
				|| role.getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
			//관리자 권한인 경우 패스
			return;
		}
		
		
		String userId = loginUser.getUserId();
		UserRoleEntity roleEntity = projectUserDomainService.getProjectUserRole(projectIdx, userId);
		
		boolean isCheck = false;
		List<UserRoleMenuEntity> list = roleEntity.getUserRoleMenus();
		for(UserRoleMenuEntity userRole: list) {
			MenuEntity memu = userRole.getMenu();
			
			if(memu.getMenuIdx().equals(menuCode)) {
				isCheck = true;
				String viewYn = userRole.getViewableYn().toUpperCase();
				String editYn = userRole.getWritableYn().toUpperCase();
				if(!(viewYn.equals("Y") || (editYn.equals("Y")))) {
					//사용 권한 없음 에러 처리					
					log.error("Detail Page - 사용자 메뉴 접근 권한 없음.");
					log.error("Detail Page - projectIdx: {}", projectIdx);
					log.error("Detail Page - userId: {}", userId);
					log.error("Detail Page - menuCode: {}", menuCode);
					log.error("Detail Page - menuPath: {}", memu.getMenuUrl());					
					throw new PermissionDenyException();
				}
				
				break;
			}
		}
		
		if(!isCheck) {
			//메뉴 코드가 없는 경우.
			log.error("Detail Page - 존재하지 않는 메뉴 코드.");
			log.error("Detail Page - menuCode: {}", menuCode);
			throw new PortalException();
		}
	}
}
