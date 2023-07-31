package kr.co.strato.portal.cluster.v1.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

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

import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.service.AddonService;
import kr.co.strato.portal.cluster.v1.model.ArgoCDInfo;
import kr.co.strato.portal.cluster.v1.model.ClusterDto;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.v1.service.ClusterService;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.ml.service.MLClusterAPIAsyncService;
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
public class ClusterController extends CommonController {

	@Autowired
	ClusterService clusterService;
	
	@Autowired
	WorkHistoryService workHistoryService;
	
	@Autowired
	private AddonService addonService;
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterService;
	
	@GetMapping("/api/v1/clusters")
    public ResponseWrapper<Page<ClusterDto.List>> getCluterList(PageRequest pageRequest){
        Page<ClusterDto.List> results = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        try {
        	results = clusterService.getClusterList(getLoginUser(), pageRequest.of());
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
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/status")
    public ResponseWrapper<ClusterDto.Status> getClusterStatus(@PathVariable(required = true) Long clusterIdx){
		ClusterDto.Status result = null;
        try {
        	result = clusterService.getClusterStatus(clusterIdx);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}        
        return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<ClusterDto.Detail> getCluster(@PathVariable(required = true) Long clusterIdx){
		ClusterDto.Detail result = null;
        
        try {
        	result = clusterService.getCluster(clusterIdx);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/summary")
    public ResponseWrapper<ClusterDto.Summary> getClusterSummary(@PathVariable(required = true) Long clusterIdx){
		ClusterDto.Summary result = null;
        
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("clusterIdx", clusterIdx);
        
        try {
        	result = clusterService.getClusterSummary(clusterIdx);
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
    public ResponseWrapper<Long> registerCluster(@Valid @RequestBody ClusterDto.Form clusterDto){
        Long result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workTarget = clusterDto.getClusterName();
        workMetadata.put("clusterDto", clusterDto);
        
        try {
        	result = clusterService.createCluster(clusterDto, getLoginUser());
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
						.workJobIdx(result)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(result);
    }

	@PutMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<Long> updateCluster(@PathVariable(required = true) Long clusterIdx, @Valid  @RequestBody ClusterDto.Form clusterDto){
        Long result = null;
        
        String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workTarget = clusterDto.getClusterName();
        workMetadata.put("clusterIdx", clusterIdx);
        workMetadata.put("clusterDto", clusterDto);
        
        try {
        	result = clusterService.updateCluster(clusterIdx, clusterDto, getLoginUser());
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
						.workJobIdx(result)
						.build());
			} catch (Exception e) {
				// ignore
			}
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@DeleteMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<Long> deleteCluster(@PathVariable(required = true) Long clusterIdx){
		Long result = null;
		
		String workTarget					= null;
        Map<String, Object> workMetadata	= new HashMap<>();
        WorkResult workResult				= WorkResult.SUCCESS;
        String workMessage					= "";
        
        workMetadata.put("clusterIdx", clusterIdx);

        try {
        	result = clusterService.deleteCluster(clusterIdx, getLoginUser());
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
						.workJobIdx(result)
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
        log.debug("[isClusterDuplication] result = {}", result);
        
        return new ResponseWrapper<>(result);
    }
	
	@PostMapping("/api/v1/clusters/connection")
    public ResponseWrapper<Boolean> isClusterConnection(@RequestBody ClusterDto.Form clusterDto){
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
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/depolyments")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterDeploymentList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterDeploymentList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/statefulsets")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterStatefulSetList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterStatefulSetList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/pods")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterPodList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterPodList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/cronjobs")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterCronJobList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterCronJobList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/jobs")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterJobList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterJobList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/replicasets")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterReplicaSetList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterReplicaSetList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/daemonsets")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterDaemonSetList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterDaemonSetList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/services")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterServiceList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterServiceList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/ingresses")
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterIngressList(@PathVariable(required = true) Long clusterIdx, PageRequest pageRequest){
		Page<ClusterNodeDto.ResListDto> results = null;
    
    	try {
			results = clusterService.getClusterIngressList(clusterIdx, pageRequest.of());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			throw new PortalException(e.getMessage());
		}
	
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}/cluster-monitoring-addon")
	public ResponseWrapper<Addon> getClusterMonitoringAddon(@PathVariable(required = true) Long clusterIdx) {
		String addonType = "cluster-monitoring";
		return new ResponseWrapper<>(addonService.getAddonByType(clusterIdx, addonType));
	}
	
	@GetMapping("/api/v1/test/delete")
	public void delete(@RequestParam Long clusterIdx) {
		clusterService.deleteClusterDB(clusterIdx);
	}
	
	/**
	 * Prometheus url 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Prometheus URL", description = "클러스터 별 Prometheus URL 요청")
	@GetMapping("/api/v1/clusters/{clusterId}/prometheusUrl")
	public ResponseWrapper<String> getPrometheusUrl(@PathVariable("clusterId") Long clusterId) {
		String url = mlClusterService.getPrometheusUrl(clusterId);
		return new ResponseWrapper<>(url);
	}
	
	/**
	 * Prometheus url 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Grafana URL", description = "클러스터 별 Grafana URL 요청")
	@GetMapping("/api/v1/clusters/{clusterId}/grafana")
	public ResponseWrapper<String> getGrafanaUrl(@PathVariable("clusterId") Long clusterId) {
		String url = mlClusterService.getGrafanaUrl(clusterId);
		return new ResponseWrapper<>(url);
	}
	
	/**
	 * Prometheus url 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "Grafana iframe URL", description = "클러스터 아이프래임 Grafana URL 요청")
	@GetMapping("/api/v1/clusters/{clusterIdx}/grafana-iframe")
	public ResponseWrapper<String> getGrafanIframeUrl(@PathVariable("clusterIdx") Long clusterIdx) {
		String url = mlClusterService.getGrafanaIframeUrl(clusterIdx);
		return new ResponseWrapper<>(url);
	}

	/**
	 * ArgoCD 접속정보 반환.
	 * @param clusterId
	 * @return
	 */
	@Operation(summary = "ArgoCD 접속 정보", description = "ArgoCD URL, Password")
	@GetMapping("/api/v1/clusters/{clusterIdx}/argocd")
	public ResponseWrapper<ArgoCDInfo> getArgoCDInfo(@PathVariable("clusterIdx") Long clusterIdx) {
		ArgoCDInfo info = mlClusterService.getArgoCDInfo(clusterIdx);
		return new ResponseWrapper<>(info);
	}
	
}
