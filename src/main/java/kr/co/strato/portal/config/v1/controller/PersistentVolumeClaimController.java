package kr.co.strato.portal.config.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.config.v1.model.PersistentVolumeClaimDto;
import kr.co.strato.portal.config.v1.service.PersistentVolumeClaimService;
import kr.co.strato.portal.work.model.WorkHistory.WorkAction;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu1;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu2;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu3;
import kr.co.strato.portal.work.model.WorkHistory.WorkResult;
import kr.co.strato.portal.work.model.WorkHistoryDto;
import kr.co.strato.portal.work.service.WorkHistoryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class PersistentVolumeClaimController extends CommonController {

	@Autowired
	PersistentVolumeClaimService persistentVolumeClaimService;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	@PostMapping("/api/v1/config/persistentVolumeClaim")
    public ResponseWrapper<List<Long>> registerPersistentVolumeClaim(@RequestBody PersistentVolumeClaimDto persistentVolumeClaimDto) {
        List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("persistentVolumeClaimDto", persistentVolumeClaimDto);
        
        try {
        	result = persistentVolumeClaimService.registerPersistentVolumeClaim(persistentVolumeClaimDto);
		} catch (Exception e) {
			e.printStackTrace();
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.CONFIG)
						.workMenu2(WorkMenu2.PERSISTENT_VOLUME_CLAIM)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.INSERT)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/config/persistentVolumeClaims")
    public ResponseWrapper<Page<PersistentVolumeClaimDto.List>> getPersistentVolumeClaimList(PageRequest pageRequest, PersistentVolumeClaimDto.Search search) {
        Page<PersistentVolumeClaimDto.List> results = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        try {
        	results = persistentVolumeClaimService.getPersistentVolumeClaimList(pageRequest.of(), search);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.CONFIG)
						.workMenu2(WorkMenu2.PERSISTENT_VOLUME_CLAIM)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.LIST)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/config/persistentVolumeClaims/{persistentVolumeClaimIdx}")
    public ResponseWrapper<PersistentVolumeClaimDto.Detail> getPersistentVolumeClaim(@PathVariable(required = true) Long persistentVolumeClaimIdx) {
		PersistentVolumeClaimDto.Detail result = null;
        
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("persistentVolumeClaimIdx", persistentVolumeClaimIdx);
        
        try {
        	result = persistentVolumeClaimService.getPersistentVolumeClaim(persistentVolumeClaimIdx, getLoginUser());
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.CONFIG)
						.workMenu2(WorkMenu2.NONE)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.DETAIL)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/config/persistentVolumeClaim/{persistentVolumeClaimIdx}/yaml")
    public ResponseWrapper<String> getPersistentVolumeClaimYaml(@PathVariable(required = true) Long persistentVolumeClaimIdx) {
		String result = null;
        
        try {
        	result = persistentVolumeClaimService.getPersistentVolumeClaimYaml(persistentVolumeClaimIdx);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@PutMapping("/api/v1/config/persistentVolumeClaim/{persistentVolumeClaimIdx}")
    public ResponseWrapper<List<Long>> updatePersistentVolumeClaim(@PathVariable(required = true) Long persistentVolumeClaimIdx, @RequestBody PersistentVolumeClaimDto persistentVolumeClaimDto) {
		List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("persistentVolumeClaimIdx", persistentVolumeClaimIdx);
        workMetadata.put("persistentVolumeClaimDto", persistentVolumeClaimDto);
        
        try {
        	result = persistentVolumeClaimService.updatePersistentVolumeClaim(persistentVolumeClaimIdx, persistentVolumeClaimDto);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.CONFIG)
						.workMenu2(WorkMenu2.PERSISTENT_VOLUME_CLAIM)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.UPDATE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@DeleteMapping("/api/v1/config/persistentVolumeClaim/{persistentVolumeClaimIdx}")
    public ResponseWrapper<Boolean> deletePersistentVolumeClaim(@PathVariable(required = true) Long persistentVolumeClaimIdx) {
		boolean result = true;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("persistentVolumeClaimIdx", persistentVolumeClaimIdx);

        try {
        	persistentVolumeClaimService.deletePersistentVolumeClaim(persistentVolumeClaimIdx);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.CONFIG)
						.workMenu2(WorkMenu2.PERSISTENT_VOLUME_CLAIM)
						.workMenu3(WorkMenu3.NONE)
						.workAction(WorkAction.DELETE)
						.target(workTarget)
						.meta(workMetadata)
						.result(workResult)
						.message(workMessage)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(result);
    }
}
