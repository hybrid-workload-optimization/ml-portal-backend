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
import kr.co.strato.portal.work.model.WorkHistory.WorkAction;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu1;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu2;
import kr.co.strato.portal.work.model.WorkHistory.WorkMenu3;
import kr.co.strato.portal.work.model.WorkHistory.WorkResult;
import kr.co.strato.portal.work.model.WorkHistoryDto;
import kr.co.strato.portal.work.service.WorkHistoryService;
import kr.co.strato.portal.workload.model.ReplicaSetDto;
import kr.co.strato.portal.workload.service.ReplicaSetService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
public class ReplicaSetController {
	
	@Autowired
	ReplicaSetService replicaSetService;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	
	@GetMapping("/api/v1/workload/replicasets")
    public ResponseWrapper<Page<ReplicaSetDto.List>> getReplicaSetList(PageRequest pageRequest, ReplicaSetDto.Search search){
        Page<ReplicaSetDto.List> results = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        try {
        	results = replicaSetService.getReplicaSetList(pageRequest.of(), search);
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
						.workMenu2(WorkMenu2.REPLICA_SET)
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
	
	@GetMapping("/api/v1/workload/replicasets/{replicaSetIdx}")
    public ResponseWrapper<ReplicaSetDto.Detail> getReplicaSet(@PathVariable(required = true) Long replicaSetIdx){
		ReplicaSetDto.Detail result = null;
        
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("replicaSetIdx", replicaSetIdx);
        
        try {
        	result = replicaSetService.getReplicaSet(replicaSetIdx);
		} catch (Exception e) {
			workResult		= WorkResult.FAIL;
			workMessage		= e.getMessage();
			
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		} finally {
			try {
				workHistoryService.registerWorkHistory(
						WorkHistoryDto.builder()
						.workMenu1(WorkMenu1.CLUSTER)
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
	
	@PostMapping("/api/v1/workload/replicasets")
    public ResponseWrapper<List<Long>> registerReplicaSet(@RequestBody ReplicaSetDto replicaSetDto){
        List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("replicaSetDto", replicaSetDto);
        
        try {
        	result = replicaSetService.registerReplicaSet(replicaSetDto);
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
						.workMenu2(WorkMenu2.REPLICA_SET)
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
	
	@PutMapping("/api/v1/workload/replicasets/{replicaSetIdx}")
    public ResponseWrapper<List<Long>> updateReplicaSet(@PathVariable(required = true) Long replicaSetIdx, @RequestBody ReplicaSetDto replicaSetDto){
		List<Long> result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("replicaSetIdx", replicaSetIdx);
        workMetadata.put("replicaSetDto", replicaSetDto);
        
        try {
        	result = replicaSetService.updateReplicaSet(replicaSetIdx, replicaSetDto);
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
						.workMenu2(WorkMenu2.REPLICA_SET)
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
	
	@DeleteMapping("/api/v1/workload/replicasets/{replicaSetIdx}")
    public ResponseWrapper<Boolean> deleteReplicaSet(@PathVariable(required = true) Long replicaSetIdx){
		boolean result = true;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("replicaSetIdx", replicaSetIdx);

        try {
        	replicaSetService.deleteReplicaSet(replicaSetIdx);
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
						.workMenu2(WorkMenu2.REPLICA_SET)
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
	
	@GetMapping("/api/v1/workload/replicasets/{replicaSetIdx}/yaml")
    public ResponseWrapper<String> getReplicaSetYaml(@PathVariable(required = true) Long replicaSetIdx){
		String result = null;
        
        try {
        	result = replicaSetService.getReplicaSetYaml(replicaSetIdx);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
}
