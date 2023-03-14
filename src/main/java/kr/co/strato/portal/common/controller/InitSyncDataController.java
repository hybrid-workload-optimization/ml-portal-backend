package kr.co.strato.portal.common.controller;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import kr.co.strato.portal.common.service.InitSyncDataService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class InitSyncDataController {

	@Value("${auth.clientId}")
	String clientId;
	
	@Autowired
	InitSyncDataService initSyncDataService; 
	
	@PostConstruct
	public void initSync() {

		// 테스트용도
		// 기존 DB 데이터 삭제 후 동기화 테스트 진행시 사용(role,user,project)
//		initSyncDataService.syncDataDelete();
		
		
		if(clientId != null && !"".equals(clientId)) {
		
			log.info("Sync data start...");
			
			// 1. Role 동기화
			initSyncDataService.syncRoleData();
			
			// 2. User 동기화
			initSyncDataService.syncUserData();

			// 3. Group 동기화
			initSyncDataService.syncGroupData();
			
			log.info("Sync data end...");
			
		}

	}
}
