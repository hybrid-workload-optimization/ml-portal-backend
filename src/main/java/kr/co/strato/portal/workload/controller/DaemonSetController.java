package kr.co.strato.portal.workload.controller;

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
import kr.co.strato.portal.work.model.WorkHistoryDto;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.work.model.WorkHistory.WorkAction;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu1;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu2;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu3;
import kr.co.strato.portal.work.model.WorkHistory.WorkResult;
import kr.co.strato.portal.work.service.WorkHistoryService;
import kr.co.strato.portal.workload.model.DaemonSetDto;
import kr.co.strato.portal.workload.model.ReplicaSetDto;
import kr.co.strato.portal.workload.service.DaemonSetService;
import kr.co.strato.portal.workload.service.ReplicaSetService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class DaemonSetController extends CommonController {

	@Autowired
	DaemonSetService daemonSetService;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	@GetMapping("/api/v1/workload/daemonsets")
    public ResponseWrapper<Page<DaemonSetDto.List>> getDaemonSetList(PageRequest pageRequest, DaemonSetDto.Search search) {
        Page<DaemonSetDto.List> results = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        try {
        	results = daemonSetService.getDaemonSetList(pageRequest.of(), search);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.WORKLOAD)
						.workMenu2(WorkMenu2.DAEMON_SET)
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
	
	@PostMapping("/api/v1/workload/daemonsets")
    public ResponseWrapper<List<Long>> registerDaemonSet(@RequestBody DaemonSetDto daemonSetDto) {
        List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("daemonSetDto", daemonSetDto);
        
        try {
        	result = daemonSetService.registerDaemonSet(daemonSetDto);
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
						.workMenu1(WorkMenu1.WORKLOAD)
						.workMenu2(WorkMenu2.DAEMON_SET)
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
	
	@GetMapping("/api/v1/workload/daemonsets/{daemonSetIdx}")
    public ResponseWrapper<DaemonSetDto.Detail> getDaemonSet(@PathVariable(required = true) Long daemonSetIdx) {
		DaemonSetDto.Detail result = null;
        
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("daemonSetIdx", daemonSetIdx);
        
        try {
        	result = daemonSetService.getDaemonSet(daemonSetIdx, getLoginUser());
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.WORKLOAD)
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
	
	@GetMapping("/api/v1/workload/daemonsets/{daemonSetIdx}/yaml")
    public ResponseWrapper<String> getDaemonSetYaml(@PathVariable(required = true) Long daemonSetIdx) {
		String result = null;
        
        try {
        	result = daemonSetService.getDaemonSetYaml(daemonSetIdx);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@PutMapping("/api/v1/workload/daemonsets/{daemonSetIdx}")
    public ResponseWrapper<List<Long>> updateDaemonSet(@PathVariable(required = true) Long daemonSetIdx, @RequestBody DaemonSetDto daemonSetDto) {
		List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("daemonSetIdx", daemonSetIdx);
        workMetadata.put("daemonSetDto", daemonSetDto);
        
        try {
        	result = daemonSetService.updateDaemonSet(daemonSetIdx, daemonSetDto);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.WORKLOAD)
						.workMenu2(WorkMenu2.DAEMON_SET)
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
	
	@DeleteMapping("/api/v1/workload/daemonsets/{daemonSetIdx}")
    public ResponseWrapper<Boolean> deleteDaemonSet(@PathVariable(required = true) Long daemonSetIdx){
		boolean result = true;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("daemonSetIdx", daemonSetIdx);

        try {
        	daemonSetService.deleteDaemonSet(daemonSetIdx);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.WORKLOAD)
						.workMenu2(WorkMenu2.DAEMON_SET)
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
