package kr.co.strato.portal.plugin.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.service.PublicClusterService;
import kr.co.strato.portal.common.service.CallbackService;
import kr.co.strato.portal.ml.model.CallbackData;
import kr.co.strato.portal.ml.service.MLInterfaceAPIAsyncService;
import kr.co.strato.portal.plugin.model.ClusterJobCallbackData;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.service.WorkJobService;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class KafkaConsumerService {
	
	@Autowired
	WorkJobService workJobService;
	
	@Autowired
	PublicClusterService publicClusterService;
	
	@Autowired
	MLInterfaceAPIAsyncService mlInterfaceApiService;
	
	@Autowired
	CallbackService callbackService;
	
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.azure.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void azureConsumer(String message) {
		log.info(message);
		try {
			messageJob(message);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.gcp.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void gcpConsumer(String message) {
		log.info(message);
		try {
			messageJob(message);
		} catch (Exception e) {
			log.error("", e);
		}
		
	}
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.aws.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void awsConsumer(String message) {
		log.info(message);
		try {
			messageJob(message);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.naver.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void naverConsumer(String message) {
		log.info(message);
		try {
			messageJob(message);
		} catch (Exception e) {
			log.error("", e);
		}
	}
	
	/**
	 * Kafka로 부터 전송되는 메시지 처리.
	 * @param message
	 */
	public void messageJob(String message) {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		
		CallbackData data = gson.fromJson(message, CallbackData.class);
		
		Long workJobIdx = data.getWorkJobIdx();
		
		int code = data.getCode();
		String status = data.getStatus();
		String msg = data.getMessage();
		Object result = data.getResult();
		
		boolean isSuccess = code == CallbackData.CODE_SUCCESS;
		
		log.info("Message queue response >>>>>>>>>>>>>>>>");
		log.info("workJobIdx: {}", workJobIdx);
		log.info("code: {}", code);
		log.info("status: {}", status);
		
		
		WorkJobEntity workJobEntity = workJobService.getWorkJob(Long.valueOf(workJobIdx));
		if(workJobEntity == null) {
			log.error("Message queue response error");
			log.error("WorkJobEntity가 존재하지 않습니다. workJobIdx: {}", workJobIdx);
			return;
		}
		
		
		WorkJobType workJobType = WorkJobType.valueOf(workJobEntity.getWorkJobType());
		String callbackUrl = workJobEntity.getCallbackUrl();
		
		
		Long clusterIdx = workJobEntity.getWorkJobReferenceIdx();
		
		//PaaS 비지니스 로직 수
		try {
			//작업하는 clusterIdx를 저장
			
			
			if(workJobType == WorkJobType.CLUSTER_CREATE) {
				if(status.equals(CallbackData.STATUS_START)) {
					publicClusterService.provisioningStart(clusterIdx, isSuccess, result);
				} else if(status.equals(CallbackData.STATUS_FINISH)) {
					ClusterEntity clusterEntity = publicClusterService.provisioningFinish(clusterIdx, isSuccess, result);
					if(clusterEntity != null) {
						mlInterfaceApiService.applyContinue(clusterEntity);
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
		} catch (Exception e) {
			log.error("", e);
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
		workJobEntity.setWorkJobMessage(msg);
		workJobEntity.setWorkJobDataResponse(response);
		workJobEntity.setWorkJobEndAt(DateUtil.currentDateTime());
		
		workJobService.updateWorkJob(workJobEntity);		
		
		//콜백이 존재하는 경우 콜백 수행
		if(callbackUrl != null) {
			
			log.info("Send Callback. url: {}", callbackUrl);
			
			String clusterJobType = workJobEntity.getWorkJobType();
			String resultStr = getResultStr(code);
			
			ClusterJobCallbackData callbackData = ClusterJobCallbackData.builder()
					.clusterIdx(clusterIdx)
					.clusterJobType(clusterJobType)
					.status(status)
					.message(msg)
					.result(resultStr)
					.build();
			
			String json = gson.toJson(callbackData);
			
			log.info("Send Callback. Data:");
			log.info(json);
			
			callbackService.sendCallback(callbackUrl, callbackData);
		}
	}
	
	/**
	 * Code에 따른 String 값 리턴.
	 * @param code
	 * @return
	 */
	public String getResultStr(int code) {		
		String result = null;
		switch (code) {
		case CallbackData.CODE_SUCCESS:
			result = "success";
			break;
		case CallbackData.CODE_FAIL:
			result = "fail";
			break;
		}
		return result;
	}
}
