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
import kr.co.strato.portal.config.service.ConfigMapService;
import kr.co.strato.portal.config.service.PersistentVolumeClaimService;
import kr.co.strato.portal.config.service.SecretService;
import kr.co.strato.portal.networking.service.IngressService;
import kr.co.strato.portal.networking.service.K8sServiceService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.workload.service.CronJobService;
import kr.co.strato.portal.workload.service.DaemonSetService;
import kr.co.strato.portal.workload.service.DeploymentService;
import kr.co.strato.portal.workload.service.JobService;
import kr.co.strato.portal.workload.service.PodService;
import kr.co.strato.portal.workload.service.ReplicaSetService;
import kr.co.strato.portal.workload.service.StatefulSetService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ProjectAuthorityService {
	
	@Autowired
	private ProjectUserDomainService projectUserDomainService;
	
	public void chechAuthority(Long projectIdx, UserDto loginUser) {
		Long menuCode = getMenuCode();
		chechAuthority(menuCode, projectIdx, loginUser);
	}
	
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
			System.out.println(memu.getMenuIdx());
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
	
	private Long getMenuCode() {
		Long menuCode = null;
		
		//WORKLOAD
		if(this instanceof DeploymentService) {
			menuCode = 103010L;
		} else if(this instanceof CronJobService) {
			menuCode = 103040L;
		} else if(this instanceof DaemonSetService) {
			menuCode = 103070L;
		} else if(this instanceof JobService) {
			menuCode = 103050L;
		} else if(this instanceof PodService) {
			menuCode = 103030L;
		} else if(this instanceof ReplicaSetService) {
			menuCode = 103060L;
		} else if(this instanceof StatefulSetService) {
			menuCode = 103020L;
		} 
		
		//CONFIG		
		else if(this instanceof PersistentVolumeClaimService) {
			menuCode = 105010L;
		}
		
		else if(this instanceof ConfigMapService) {
			menuCode = 105020L;
		}
		
		else if(this instanceof SecretService) {
			menuCode = 105030L;
		}
		
		//NETWORKING
		else if(this instanceof IngressService) {
			menuCode = 104020L;
		} else if(this instanceof K8sServiceService) {
			menuCode = 104010L;
		}
		
		return menuCode;
	}
}
