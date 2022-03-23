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
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.service.WorkJobDomainService;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.work.model.WorkJob.WorkJobData;
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
	ClusterDomainService clusterJobDomainService;
	
	@Autowired
	ClusterAdapterService clusterAdapterService;
	
	
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
		try {
			workJobDataRequest = mapper.readValue(workJobEntity.getWorkJobDataRequest(), new TypeReference<HashMap<String, Object>>(){});
		} catch (Exception e) {
			log.warn("[callbackWorkJob] Can not convert to workJobDataRequest");
			log.warn("[callbackWorkJob]", e);
		}
		
		Map<String, Object> workJobDataRequestHeader = null;
		Map<String, Object> workJobDataRequestQuery = null;
		Map<String, Object> workJobDataRequestPath = null;
		Map<String, Object> workJobDataRequestBody = null;
		if (workJobDataRequest != null) {
			workJobDataRequestHeader	= (Map<String, Object>) workJobDataRequest.get(WorkJobData.HEADER.name());
			workJobDataRequestQuery		= (Map<String, Object>) workJobDataRequest.get(WorkJobData.QUERY.name());
			workJobDataRequestPath		= (Map<String, Object>) workJobDataRequest.get(WorkJobData.PATH.name());
			workJobDataRequestBody		= (Map<String, Object>) workJobDataRequest.get(WorkJobData.BODY.name());
		}
		
		// main work_job
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
					
					ProvisioningStatus provisioningStatus = ClusterEntity.ProvisioningStatus.valueOf(state.toUpperCase());
					
					// k8s - get cluster's information(health + version)
					String providerVersion = null;
					if (workJobStatus == WorkJobStatus.SUCCESS && provisioningStatus == ProvisioningStatus.FINISHED) {
						ClusterInfoAdapterDto clusterInfo = clusterAdapterService.getClusterInfo(clusterId);
						
						providerVersion = clusterInfo.getKubeletVersion();
						log.info("[callbackWorkJob] providerVersion : {}", providerVersion);	
					}
					
					// update cluster
					ClusterEntity clusterEntity = clusterJobDomainService.get(workJobEntity.getWorkJobReferenceIdx());
					clusterEntity.setClusterId(clusterId);
					clusterEntity.setProvisioningLog(provisioningLog);
					clusterEntity.setProviderVersion(providerVersion);
					if (workJobStatus == WorkJobStatus.FAIL) {
						clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
					} else {
						clusterEntity.setProvisioningStatus(provisioningStatus.name());
					}
					
					clusterJobDomainService.update(clusterEntity);
				}
			} catch (Exception e) {
				log.error("[callbackWorkJob] Cluster creation failed");
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
