package kr.co.strato.portal.work.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.adapter.k8s.cluster.model.ClusterInfoAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.service.WorkJobDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.service.ClusterSyncService;
import kr.co.strato.portal.work.model.WorkJob;
import kr.co.strato.portal.work.model.WorkJob.WorkJobData;
import kr.co.strato.portal.work.model.WorkJob.WorkJobState;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.model.WorkJobCallback;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkJobCallbackService {
	
	@Autowired
	WorkJobDomainService workJobDomainService;
	
	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ClusterAdapterService clusterAdapterService;
	
	@Autowired
	ClusterSyncService clusterSyncService;
	
	@Autowired
	NodeDomainService nodeDomainService;
	
	
	public void callbackWorkJob(WorkJobCallback<Map<String, Object>> workJobCallback) {
		String result				= workJobCallback.getResult();
		String message				= workJobCallback.getMessage();
		Map<String, Object> data	= workJobCallback.getData();
		
		log.info("[callbackWorkJob] started >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		log.info("[callbackWorkJob] result/message/data = {}/{}/{}", result, message, data);
		
		if (CollectionUtils.isEmpty(data)) {
			throw new PortalException("[callbackWorkJob] data is empty");
		}
		
		Integer workJobIdx = (Integer) data.get("workJobIdx");
		if (workJobIdx == null) {
			throw new PortalException("[callbackWorkJob] workJobIdx is null");
		}
		
		// get work_job information
		WorkJobEntity workJobEntity = workJobDomainService.get(Long.valueOf(workJobIdx));
		if (workJobEntity == null) {
			throw new PortalException("[callbackWorkJob] workJobIdx do not exist");
		}
		
		String workJobTarget			= (String) data.get("workJobTarget");
		Map<String, Object> workJobData	= (Map<String, Object>) data.get("workJobData");
		
		WorkJobType workJobType			= WorkJobType.valueOf(workJobEntity.getWorkJobType());
		WorkJobStatus workJobStatus		= WorkJobStatus.valueOf(result.toUpperCase());
		
		// Request Data
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		
		Map<String, Object> workJobDataRequest = null;
		Map<String, Object> workJobDataRequestHeader = null;
		Map<String, Object> workJobDataRequestQuery = null;
		Map<String, Object> workJobDataRequestPath = null;
		Map<String, Object> workJobDataRequestBody = null;
		
		if (workJobEntity.getWorkJobDataRequest() != null) {
			try {
				workJobDataRequest = mapper.readValue(workJobEntity.getWorkJobDataRequest(), new TypeReference<HashMap<String, Object>>(){});
			} catch (Exception e) {
				log.warn("[callbackWorkJob] Can not convert to workJobDataRequest");
				log.warn("[callbackWorkJob]", e);
			}
			
			if (workJobDataRequest != null) {
				workJobDataRequestHeader	= (Map<String, Object>) workJobDataRequest.get(WorkJobData.HEADER.name());
				workJobDataRequestQuery		= (Map<String, Object>) workJobDataRequest.get(WorkJobData.QUERY.name());
				workJobDataRequestPath		= (Map<String, Object>) workJobDataRequest.get(WorkJobData.PATH.name());
				workJobDataRequestBody		= (Map<String, Object>) workJobDataRequest.get(WorkJobData.BODY.name());
			}
		}
		
		// main work_job
		log.info("[callbackWorkJob] workJobType={}", workJobType.name());
		
		if (workJobType == WorkJobType.CLUSTER_CREATE) {
			try {
				// Response Data 
				if (workJobData != null) {
					Integer kubeConfigId	= (Integer) workJobData.get("kubeConfigId");
					String provisioningLog	= (String) workJobData.get("log");
					String state			= (String) workJobData.get("state");
					
					Long clusterId = null;
					if (kubeConfigId != null) {
						clusterId = Long.valueOf(kubeConfigId);
						log.info("[callbackWorkJob] clusterId : {}", clusterId);	
					}
					
					WorkJobState workJobState = WorkJob.WorkJobState.valueOf(state.toUpperCase());
					
					// k8s - get cluster's information(health + version)
					String providerVersion = null;
					if (workJobStatus == WorkJobStatus.SUCCESS && workJobState == WorkJobState.FINISHED) {
						ClusterInfoAdapterDto clusterInfo = clusterAdapterService.getClusterInfo(clusterId);
						
						providerVersion = clusterInfo.getKubeletVersion();
						log.info("[callbackWorkJob] providerVersion : {}", providerVersion);
						
						// db - 생성시 등록한 node 정보 삭제
						/*
						ClusterEntity clusterEntity = clusterDomainService.get(workJobEntity.getWorkJobReferenceIdx());
						for (NodeEntity nodeEntity : clusterEntity.getNodes() ) {
							nodeDomainService.delete(nodeEntity.getId());
						}
						*/
						// db - sync(insert) cluster - node/namespace/pv/storageClass
						clusterSyncService.syncCluster(clusterId, workJobEntity.getWorkJobReferenceIdx());
					}
					
					// db - update cluster
					ClusterEntity clusterEntity = clusterDomainService.get(workJobEntity.getWorkJobReferenceIdx());
					clusterEntity.setClusterId(clusterId);
					clusterEntity.setProvisioningLog(provisioningLog);
					clusterEntity.setProviderVersion(providerVersion);
					if (workJobStatus == WorkJobStatus.FAIL) {
						clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
					} else {
						clusterEntity.setProvisioningStatus(state.toUpperCase());
					}
					
					clusterDomainService.update(clusterEntity);
				}
			} catch (Exception e) {
				log.error("[callbackWorkJob] Cluster creation failed");
				log.error("[callbackWorkJob]", e);
			}
		} else if (workJobType == WorkJobType.CLUSTER_SCALE) {
			try {
				// Response Data 
				if (workJobData != null) {
					String workJobDataLog	= (String) workJobData.get("log");
					String state			= (String) workJobData.get("state");
					
					WorkJobState workJobState = WorkJob.WorkJobState.valueOf(state.toUpperCase());
					if (workJobStatus == WorkJobStatus.SUCCESS && workJobState == WorkJobState.FINISHED) {
						// db - sync(update) node
						clusterSyncService.syncClusterNode(workJobEntity.getWorkJobReferenceIdx());
					}
				} 
			} catch (Exception e) {
				log.error("[callbackWorkJob] Cluster scale failed");
				log.error("[callbackWorkJob]", e);
			}
		} else if (workJobType == WorkJobType.CLUSTER_DELETE) {
			try {
				// Response Data 
				if (workJobData != null) {
					String workJobDataLog	= (String) workJobData.get("log");
					String state			= (String) workJobData.get("state");
					
					WorkJobState workJobState = WorkJob.WorkJobState.valueOf(state.toUpperCase());
					if (workJobStatus == WorkJobStatus.SUCCESS && workJobState == WorkJobState.FINISHED) {
						// db - delete cluster
						ClusterEntity clusterEntity = clusterDomainService.get(workJobEntity.getWorkJobReferenceIdx());
						clusterDomainService.delete(clusterEntity);
					}
				}
			} catch (Exception e) {
				log.error("[callbackWorkJob] Cluster deletion failed");
				log.error("[callbackWorkJob]", e);
			}
		}
		
		// update work history ?
		
		// update work job
		String workJobResponse = null;
		try {
			Map<String, Object> workJobDataResponse	= new HashMap<>();
			workJobDataResponse.put(WorkJobData.BODY.name(), workJobCallback);
			
			workJobResponse = new ObjectMapper().writeValueAsString(workJobDataResponse);
			log.info("[callbackWorkJob] work job response : {}", workJobResponse);
		} catch (JsonProcessingException e) {
			log.warn("[callbackWorkJob] Can not convert to work job response");
			log.warn("[callbackWorkJob]", e);
		}
		
		workJobEntity.setWorkJobStatus(workJobStatus.name());
		workJobEntity.setWorkJobMessage(message);
		workJobEntity.setWorkJobDataResponse(workJobResponse);
		workJobEntity.setWorkJobEndAt(DateUtil.currentDateTime());
		
		workJobDomainService.update(workJobEntity);
	}

}
