package kr.co.strato.portal.ml.v1.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.v1.service.PublicClusterService;
import kr.co.strato.portal.ml.v1.model.CallbackData;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.service.WorkJobService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterJobCallbackService {
	
	@Autowired
	WorkJobService workJobService;
	
	@Autowired
	PublicClusterService publicClusterService;
	
	@Autowired
	MLInterfaceAPIAsyncService mlInterfaceApiService;

	/**
	 * callback 작업 수행
	 * @param data
	 */
	public void callback(CallbackData data) {		
		Long workJobIdx = data.getWorkJobIdx();
		
		int code = data.getCode();
		String status = data.getStatus();
		String message = data.getMessage();
		Object result = data.getResult();
		
		boolean isSuccess = code == CallbackData.CODE_SUCCESS;
		
		log.info("Callback 수신 >>>>>>>>>>>>>>>>");
		log.info("workJobIdx: {}", workJobIdx);
		log.info("code: {}", code);
		log.info("status: {}", status);
		
		
		WorkJobEntity workJobEntity = workJobService.getWorkJob(Long.valueOf(workJobIdx));
		WorkJobType workJobType = WorkJobType.valueOf(workJobEntity.getWorkJobType());
		
		//작업하는 clusterIdx를 저장
		Long clusterIdx = workJobEntity.getWorkJobReferenceIdx();
		
		if(workJobType == WorkJobType.CLUSTER_CREATE) {
			if(status.equals(CallbackData.STATUS_START)) {
				publicClusterService.provisioningStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				ClusterEntity mlClusterEntity = publicClusterService.provisioningFinish(clusterIdx, isSuccess, result);
				if(mlClusterEntity != null) {
					mlInterfaceApiService.applyContinue(mlClusterEntity);
				} 
			}
		} else if(workJobType == WorkJobType.CLUSTER_DELETE) {
			if(status.equals(CallbackData.STATUS_START)) {
				publicClusterService.deleteStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				mlInterfaceApiService.deletePre(clusterIdx);
				publicClusterService.deleteFinish(clusterIdx, isSuccess, result);
			}
		} else if(workJobType == WorkJobType.CLUSTER_SCALE) {
			if(status.equals(CallbackData.STATUS_START)) {
				publicClusterService.scaleStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				publicClusterService.scaleFinish(clusterIdx, isSuccess, result);
			}
		} else if(workJobType == WorkJobType.CLUSTER_MODIFY) {
			if(status.equals(CallbackData.STATUS_START)) {
				publicClusterService.modifyStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				publicClusterService.modifyFinish(clusterIdx, isSuccess, result);
			}
		}		
		
		//workJob 업데이트
		String response = null;
		if(result != null) {
			try {
				response = new ObjectMapper().writeValueAsString(result);
			} catch (JsonProcessingException e) {
				log.error("", e);
			}
		}	
		
		workJobEntity.setWorkJobStatus(WorkJobStatus.STARTED.name());
		workJobEntity.setWorkJobMessage(message);
		workJobEntity.setWorkJobDataResponse(response);
		workJobEntity.setWorkJobEndAt(DateUtil.currentDateTime());
		
		workJobService.updateWorkJob(workJobEntity);
	}
}
