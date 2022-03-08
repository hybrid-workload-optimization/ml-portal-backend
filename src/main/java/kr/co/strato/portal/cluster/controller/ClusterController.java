package kr.co.strato.portal.cluster.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.service.ClusterService;
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
public class ClusterController {

	@Autowired
	ClusterService clusterService;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	@GetMapping("/api/v1/clusters")
    public ResponseWrapper<Page<ClusterDto>> getCluterList(PageRequest pageRequest){
        Page<ClusterDto> results = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        try {
        	results = clusterService.getClusterList(pageRequest.of());
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
	
	@GetMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<ClusterDto> getCluster(@PathVariable(required = true) Long clusterIdx){
		ClusterDto result = null;
        
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("clusterIdx", clusterIdx);
        
        try {
        	result = clusterService.getCluster(clusterIdx);
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
	
	@PostMapping("/api/v1/clusters")
    public ResponseWrapper<Long> registerCluster(@RequestBody ClusterDto clusterDto){
        Long result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workTarget = clusterDto.getClusterName();
        workMetadata.put("clusterDto", clusterDto);
        
        try {
        	result = clusterService.registerCluster(clusterDto);
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

	@PutMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<Long> updateCluster(@PathVariable(required = true) Long clusterIdx, @RequestBody ClusterDto clusterDto){
        Long result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workTarget = clusterDto.getClusterName();
        workMetadata.put("clusterIdx", clusterIdx);
        workMetadata.put("clusterDto", clusterDto);
        
        try {
        	result = clusterService.updateCluster(clusterIdx, clusterDto);
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
	
	@DeleteMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<Boolean> deleteCluster(@PathVariable(required = true) Long clusterIdx){
		boolean result = true;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("clusterIdx", clusterIdx);

        try {
        	clusterService.deleteCluster(clusterIdx);
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
	
	@GetMapping("/api/v1/clusters/duplication")
    public ResponseWrapper<Boolean> isClusterDuplication(@RequestParam(required = true) String name){
		boolean result = false;
        
        try {
        	result = clusterService.isClusterDuplication(name);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@PostMapping("/api/v1/clusters/connection")
    public ResponseWrapper<Boolean> isClusterConnection(@RequestBody ClusterDto clusterDto){
		boolean result = false;
        
        try {
        	result = clusterService.isClusterConnection(clusterDto.getKubeConfig());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/nodes")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterNodeList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterNodeList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
}
