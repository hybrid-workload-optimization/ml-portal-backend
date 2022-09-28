package kr.co.strato.portal.ml.service;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.ml.model.CallbackData;
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
	MLClusterAPIAsyncService mlClusterApiService;
	
	@Autowired
	MLInterfaceAPIAsyncService mlInterfaceApiService;
	
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.azure.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void azureConsumer(String message) throws IOException {
		messageJob(message);
	}
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.gcp.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void gcpConsumer(String message) throws IOException {
		messageJob(message);
	}
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.aws.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void awsConsumer(String message) throws IOException {
		messageJob(message);
	}
	
	@KafkaListener(
			topics = "${plugin.kafka.topic.naver.response}", 
			groupId = "${plugin.kafka.paas-portal.consumer.group}", 
			containerFactory = "kafkaListenerContainerFactory")
    public void naverConsumer(String message) throws IOException {
		messageJob(message);
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
		
		//작업하는 clusterIdx를 저장
		Long clusterIdx = workJobEntity.getWorkJobReferenceIdx();
		
		if(workJobType == WorkJobType.CLUSTER_CREATE) {
			if(status.equals(CallbackData.STATUS_START)) {
				mlClusterApiService.provisioningStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				MLClusterEntity mlClusterEntity = mlClusterApiService.provisioningFinish(clusterIdx, isSuccess, result);
				if(mlClusterEntity != null) {
					mlInterfaceApiService.applyContinue(mlClusterEntity);
				} 
			}
		} else if(workJobType == WorkJobType.CLUSTER_DELETE) {
			if(status.equals(CallbackData.STATUS_START)) {
				mlClusterApiService.deleteStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				mlInterfaceApiService.deletePre(clusterIdx);
				mlClusterApiService.deleteFinish(clusterIdx, isSuccess, result);
			}
		} else if(workJobType == WorkJobType.CLUSTER_SCALE) {
			if(status.equals(CallbackData.STATUS_START)) {
				mlClusterApiService.scaleStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				mlClusterApiService.scaleFinish(clusterIdx, isSuccess, result);
			}
		} else if(workJobType == WorkJobType.CLUSTER_MODIFY) {
			if(status.equals(CallbackData.STATUS_START)) {
				mlClusterApiService.modifyStart(clusterIdx, isSuccess, result);
			} else if(status.equals(CallbackData.STATUS_FINISH)) {
				mlClusterApiService.modifyFinish(clusterIdx, isSuccess, result);
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
		workJobEntity.setWorkJobMessage(msg);
		workJobEntity.setWorkJobDataResponse(response);
		workJobEntity.setWorkJobEndAt(DateUtil.currentDateTime());
		
		workJobService.updateWorkJob(workJobEntity);
	}
}
