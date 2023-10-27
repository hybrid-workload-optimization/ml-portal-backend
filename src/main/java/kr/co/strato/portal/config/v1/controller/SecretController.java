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
import kr.co.strato.portal.config.v1.model.ConfigMapDto;
import kr.co.strato.portal.config.v1.model.SecretDto;
import kr.co.strato.portal.config.v1.service.ConfigMapService;
import kr.co.strato.portal.config.v1.service.SecretService;
import kr.co.strato.portal.work.model.WorkHistoryDto;
import kr.co.strato.portal.work.model.WorkHistory.WorkAction;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu1;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu2;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu3;
import kr.co.strato.portal.work.model.WorkHistory.WorkResult;
import kr.co.strato.portal.work.service.WorkHistoryService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class SecretController extends CommonController {

	@Autowired
	SecretService secretService;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	@PostMapping("/api/v1/config/secret")
    public ResponseWrapper<List<Long>> registerSecret(@RequestBody SecretDto secretDto) {
        List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("secretDto", secretDto);
        
        try {
        	result = secretService.registerSecret(secretDto);
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
						.workMenu2(WorkMenu2.SECRET)
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
	
	@GetMapping("/api/v1/config/secret")
    public ResponseWrapper<Page<SecretDto.List>> getSecretList(PageRequest pageRequest, SecretDto.Search search) {
        Page<SecretDto.List> results = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        try {
        	results = secretService.getSecretList(pageRequest.of(), search);
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
						.workMenu2(WorkMenu2.SECRET)
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
	
	@GetMapping("/api/v1/config/secret/{secretIdx}")
    public ResponseWrapper<SecretDto.Detail> getSecret(@PathVariable(required = true) Long secretIdx) {
		SecretDto.Detail result = null;
        
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("secretIdx", secretIdx);
        
        try {
        	result = secretService.getSecret(secretIdx, getLoginUser());
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
	
	@GetMapping("/api/v1/config/secret/{secretIdx}/yaml")
    public ResponseWrapper<String> getSecertYaml(@PathVariable(required = true) Long secretIdx) {
		String result = null;
        
        try {
        	result = secretService.getSecretYaml(secretIdx);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@PutMapping("/api/v1/config/secret/{secretIdx}")
    public ResponseWrapper<List<Long>> updateSecret(@PathVariable(required = true) Long secretIdx, @RequestBody SecretDto secretDto) {
		List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("secretIdx", secretIdx);
        workMetadata.put("secretDto", secretDto);
        
        try {
        	result = secretService.updateSecret(secretIdx, secretDto);
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
						.workMenu2(WorkMenu2.SECRET)
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
	
	@DeleteMapping("/api/v1/config/secret/{secretIdx}")
    public ResponseWrapper<Boolean> deleteSecret(@PathVariable(required = true) Long secretIdx) {
		boolean result = true;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("secretIdx", secretIdx);

        try {
        	secretService.deleteSecret(secretIdx);
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
						.workMenu2(WorkMenu2.SECRET)
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
