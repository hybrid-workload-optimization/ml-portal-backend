package kr.co.strato.portal.cluster.service;

import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;
import kr.co.strato.adapter.cloud.common.service.CloudAdapterService;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningType;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ModifyArgDto;
import kr.co.strato.portal.cluster.model.PublicClusterDto;
import kr.co.strato.portal.cluster.model.ScaleArgDto;
import kr.co.strato.portal.ml.model.MessageData;
import kr.co.strato.portal.plugin.service.KafkaProducerService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.service.MLSettingService;
import kr.co.strato.portal.work.model.WorkJobDto;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.service.WorkJobService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class PublicClusterService {
	
	@Autowired
	private CloudAdapterService cloudAdapterService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private WorkJobService workJobService;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	@Autowired
	private MLSettingService mlSettingService;
	
	@Autowired
	private ClusterSyncService clusterSyncService;
	
	@Autowired
	private ClusterAdapterService clusterAdapterService;
	
	@Autowired
	private Environment env;
	
	public ClusterEntity provisioningCluster(PublicClusterDto.Povisioning param, UserDto user) {
		return provisioningCluster(param, user, null);
	}
	

	/**
	 * Public 클러스터 생성.
	 * @param param
	 * @return
	 */
    public ClusterEntity provisioningCluster(PublicClusterDto.Povisioning param, UserDto user, Map<String, Object> header) {
    	log.info("[ProvisioningCluster] - start");
    	String callbackUrl = param.getCallbackUrl();
		String cloudProvider = param.getCloudProvider().toLowerCase();
		
		Map<String, Object> provisioningParam = param.getPovisioningParam();		
		
		AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
		boolean isVaild = paramProvider.isVaildProvisioningParam(provisioningParam);
		
		if(!isVaild) {
			log.error("[ProvisioningCluster] - 클러스터 생성 실패!");
			log.error("[ProvisioningCluster] - 유효하지 않은 파라메터.");
			throw new BadRequestException("Invalid parameter.");
		}
		
		String clusterName = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_CLUSTER_NAME);
		String clusterDesc = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_CLUSTER_DESC);
		String region = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_REGION);
		String kubeletVersion = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_KUBERNETES_VERSION);
		String vmType =  (String)((Map)((List)provisioningParam.get(AbstractDefaultParamProvider.KEY_NODE_POOLS)).get(0)).get(AbstractDefaultParamProvider.KEY_VM_TYPE);
		int nodeCount = (int)((Map)((List)provisioningParam.get(AbstractDefaultParamProvider.KEY_NODE_POOLS)).get(0)).get(AbstractDefaultParamProvider.KEY_NODE_COUNT);
		
		String now = DateUtil.currentDateTime();
		ClusterEntity clusterEntity = ClusterEntity.builder()
				.clusterName(clusterName)
				.description(clusterDesc)
				.createdAt(now)
				.provider(cloudProvider)
				.providerVersion(kubeletVersion)
				.provisioningStatus(ClusterEntity.ProvisioningStatus.PENDING.name())
				.createUserId(user.getUserId())
				.createUserName(user.getUserName())
				.provisioningType(getProvisiongType(cloudProvider))
				.nodeCount(nodeCount)
				.vmType(vmType)
				.region(region)
				.build();		
		clusterDomainService.register(clusterEntity);
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String createParamJson = gson.toJson(provisioningParam);
		log.info("Request Param - Provisioning cluster");
		log.info(createParamJson);
		
		
		//WorkJob 등록
		WorkJobDto workJobDto = WorkJobDto.builder().
				workJobTarget(clusterName)
				.workJobType(WorkJobType.CLUSTER_CREATE)
				.workJobStatus(WorkJobStatus.WAITING)
				.workJobStartAt(DateUtil.currentDateTime())
				.workSyncYn("N")
				.workJobReferenceIdx(clusterEntity.getClusterIdx())
				.createUserId(user.getUserId())
				.createUserName(user.getUserName())
				.callbackUrl(callbackUrl)
				.build();
		
		Long workJobIdx = workJobService.registerWorkJob(workJobDto);
		log.info("[ProvisioningCluster] work job idx : {}", workJobIdx);
		
		
		//kafka에 넣기 위한 데이터.
		MessageData messageData = MessageData.builder()
				.workJobIdx(workJobIdx)
				.jobType(MessageData.JOB_TYPE_PROVISIONING)
				.header(header)
				.param(provisioningParam)
				.build();
		
		String messageDataJson = gson.toJson(messageData);
		log.info("[ProvisioningCluster]  메세지 큐 등록");
		log.info(messageDataJson);
		
		//kafka 전송
		kafkaProducerService.sendMessage(getCloudRequestTopic(cloudProvider), messageDataJson);
		return clusterEntity;
	}
    
    public boolean deleteCluster(PublicClusterDto.Delete deleteParam, UserDto user) {
    	return deleteCluster(deleteParam, user, null);
    }
	

    /**
     * Public 클러스터 삭제.
     * @param param
     * @return
     */
    public boolean deleteCluster(PublicClusterDto.Delete deleteParam, UserDto user, Map<String, Object> header) {
    	String callbackUrl = deleteParam.getCallbackUrl();
		Long clusterIdx = deleteParam.getClusterIdx();
		
		log.info("[DeleteCluster] - start");
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		if(clusterEntity != null) {
			
			String now = DateUtil.currentDateTime();
			clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.DELETING.name());
			clusterEntity.setUpdatedAt(now);
			
			clusterDomainService.update(clusterEntity);
			
			String clusterName = clusterEntity.getClusterName();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			//WorkJob 등록
			WorkJobDto workJobDto = WorkJobDto.builder().
					workJobTarget(clusterName)
					.workJobType(WorkJobType.CLUSTER_DELETE)
					.workJobStatus(WorkJobStatus.WAITING)
					.workJobStartAt(DateUtil.currentDateTime())
					.workSyncYn("N")
					.workJobReferenceIdx(clusterEntity.getClusterIdx())
					.createUserId(user.getUserId())
					.createUserName(user.getUserName())
					.callbackUrl(callbackUrl)
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[DeleteCluster] work job idx : {}", workJobIdx);
			
			String cloudProvider = clusterEntity.getProvider();
			AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
			
			Map<String, Object> param = paramProvider.genDeleteParam(clusterName, null);
			
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_DELETE)
					.header(header)
					.param(param)
					.build();
			
			String messageDataJson = gson.toJson(messageData);
			log.info("[DeleteCluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(cloudProvider), messageDataJson);
			return true;
		} else {
			log.error("[DeleteCluster] - 시작 실패");
			log.error("[DeleteCluster] - 클러스터가 존재하지 않습니다. clusterIdx: {}", clusterIdx);
		}
		return false;
	}
    
    public void scaleJobCluster(ScaleArgDto scaleDto, UserDto user) {
    	scaleJobCluster(scaleDto, user, null);
    }
    
    
    /**
	 * Job 클러스터 프로비저닝
	 * @param yaml
	 * @return
	 */
	public void scaleJobCluster(ScaleArgDto scaleDto, UserDto user, Map<String, Object> header) {
		log.info("[Scale Cluster]  작업 시작");
		Long clusterIdx = scaleDto.getClusterIdx();
		String callbackUrl = scaleDto.getCallbackUrl();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		
		if(entity != null) {
			String clusterName = entity.getClusterName();
			Integer nodeCount = scaleDto.getNodeCount();
						
			//WorkJob 등록
			WorkJobDto workJobDto = WorkJobDto.builder().
					workJobTarget(clusterName)
					.workJobType(WorkJobType.CLUSTER_SCALE)
					.workJobStatus(WorkJobStatus.WAITING)
					.workJobStartAt(DateUtil.currentDateTime())
					.workSyncYn("N")
					.workJobReferenceIdx(entity.getClusterIdx())
					.createUserId(user.getUserId())
					.createUserName(user.getUserName())
					.callbackUrl(callbackUrl)
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[Scale Cluster] work job idx : {}", workJobIdx);
			
			String cloudProvider = entity.getProvider();
			AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
			
			Map<String, Object> param = paramProvider.genScaleParam(clusterName, null, nodeCount);
			
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_SCALE)
					.header(header)
					.param(param)
					.build();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String messageDataJson = gson.toJson(messageData);
			log.info("[Scale Cluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(cloudProvider), messageDataJson);
			
		} else {
			log.error("[Scale Cluster] 작업 실패!");
			log.error("[Scale Cluster] Cluster를 찾을 수 없습니다. clusterIdx: {}", clusterIdx);
		}
		
	}
	
	public void modifyJobCluster(ModifyArgDto modifyDto, UserDto user) {
		modifyJobCluster(modifyDto, user, null);
	}
	
	
	/**
	 * 노드풀 변경 작업.
	 * @param modifyDto
	 */
	public void modifyJobCluster(ModifyArgDto modifyDto, UserDto user, Map<String, Object> header) {
		log.info("[Modify Cluster]  작업 시작");
		Long clusterIdx = modifyDto.getClusterIdx();
		String callbackUrl = modifyDto.getCallbackUrl();
		ClusterEntity entity = clusterDomainService.get(clusterIdx);
		
		if(entity != null) {
			String clusterName = entity.getClusterName();
			
			String vmType = modifyDto.getVmType();
			Integer nodeCount = modifyDto.getNodeCount();
						
			//WorkJob 등록
			WorkJobDto workJobDto = WorkJobDto.builder().
					workJobTarget(clusterName)
					.workJobType(WorkJobType.CLUSTER_MODIFY)
					.workJobStatus(WorkJobStatus.WAITING)
					.workJobStartAt(DateUtil.currentDateTime())
					.workSyncYn("N")
					.workJobReferenceIdx(clusterIdx)
					.createUserId(user.getUserId())
					.createUserName(user.getUserName())
					.callbackUrl(callbackUrl)
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[Modify Cluster] work job idx : {}", workJobIdx);
			
			
			String cloudProvider = entity.getProvider();
			AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
			
			Map<String, Object> param = paramProvider.genModifyParam(clusterName, null, vmType, nodeCount);
			
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_MODIFY)
					.header(header)
					.param(param)
					.build();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String messageDataJson = gson.toJson(messageData);
			log.info("[Modify Cluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(cloudProvider), messageDataJson);
			
		} else {
			log.error("[Modify Cluster] 작업 실패!");
			log.error("[Modify Cluster] Cluster를 찾을 수 없습니다. clusterIdx: {}", clusterIdx);
		}
	}
	
	/**
	 * 클러스터 생성 완료 후 작업
	 * @param clusterEntity
	 * @param kubeConfig
	 * @return
	 */
	public boolean finishJobCluster(ClusterEntity clusterEntity, String kubeConfig) {
		String cloudVender = mlSettingService.getCloudProvider();
		
		try {
			ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
					.provider(cloudVender)
					.configContents(Base64.getEncoder().encodeToString(kubeConfig.getBytes()))
					.build();
			
			String strClusterId = clusterAdapterService.registerCluster(clusterAdapterDto);
			
			if (StringUtils.isEmpty(strClusterId)) {
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
			} else {
				Long kubeConfigId = Long.valueOf(strClusterId);
				clusterEntity.setClusterId(kubeConfigId);
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
				
				
				log.info("Cluster Synchronization started.");
				clusterSyncService.syncCluster(kubeConfigId, clusterEntity.getClusterIdx());
				log.info("Cluster Synchronization finished.");
			}
			
			clusterDomainService.update(clusterEntity);
			log.info("Job cluster provisioning success.");
			
		} catch (Exception e) {
			log.error("[finishJobCluster] - fail");
			log.error("", e);
			return false;
		}
		return true;
	}

	
	/**
	 * 클러스터 프로비저닝 시작 콜백
	 * @param clusterIdx
	 * @param isSuccess
	 */
	public void provisioningStart(Long clusterIdx, boolean isSuccess, Object data) {
		log.info("Callback process - provisioning start");
		
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		if(cluster != null) {
			ProvisioningStatus status = ProvisioningStatus.STARTED;
			String now = DateUtil.currentDateTime();
			if(!isSuccess) {
				status = ProvisioningStatus.FAILED;
			}			
			cluster.setProvisioningStatus(status.name());
			cluster.setUpdatedAt(now);
			clusterDomainService.update(cluster);
		}
	}
	
	/**
	 * 클러스터 프로비저닝 종료 콜백
	 * @param clusterIdx
	 * @param isSuccess
	 * @param data
	 */
	public ClusterEntity provisioningFinish(Long clusterIdx, boolean isSuccess, Object data) {
		log.info("Callback process - provisioning finish");
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		String cloudVender = mlSettingService.getCloudProvider();
		if(clusterEntity != null) {
			
			if(isSuccess && data != null && data instanceof String) {
				String kubeConfig = (String) data;
				try {
					ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
							.provider(cloudVender)
							.configContents(Base64.getEncoder().encodeToString(kubeConfig.getBytes()))
							.build();
					
					String strClusterId = clusterAdapterService.registerCluster(clusterAdapterDto);
					
					if (StringUtils.isEmpty(strClusterId)) {
						clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
						
						log.error("KubeConfig 등록 실패");
					} else {
						Long kubeConfigId = Long.valueOf(strClusterId);
						clusterEntity.setClusterId(kubeConfigId);
						clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
						
						log.info("KubeConfig 등록 완료");
						log.info("Cluster Synchronization started.");
						clusterSyncService.syncCluster(kubeConfigId, clusterEntity.getClusterIdx());
						log.info("Cluster Synchronization finished.");
					}
				} catch (Exception e) {
					log.error("", e);
				}
			} else {
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
				
				log.error("Cluster 생성 실패 했거나 KubeConfig 데이터가 잘못 되었습니다.");
				log.error("data: {}", data);
			}
		} else {			
			log.error("Cluster가 존재하지 않습니다.");
			log.error("clusterIdx: {}", clusterIdx);
		}
		
		String now = DateUtil.currentDateTime();
		clusterEntity.setUpdatedAt(now);
		
		clusterDomainService.update(clusterEntity);
		log.info("Job cluster provisioning success.");
		return clusterEntity;
	}
	
	/**
	 * 클러스터 삭제 시작
	 * @param clusterIdx
	 * @param isSuccess
	 * @param data
	 */
	public void deleteStart(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.DELETING;
		 
		if(!isSuccess) {
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, clusterStatus);
	}
	
	public void deleteFinish(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		String clusterName = clusterEntity.getClusterName();
		
		if(isSuccess) {		
			log.info("Delete cluster finish: {}, Provider: {}", clusterName, clusterEntity.getProvider());
		} else {
			log.info("Delete cluster fail: {}, Provider: {}", clusterName, clusterEntity.getProvider());
		}
		
		//클러스터 삭제.
		clusterDomainService.delete(clusterEntity);
	}
	
	public void scaleStart(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.SCALE;		 
		if(!isSuccess) {
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, clusterStatus);
	}
	
	public void scaleFinish(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.FINISHED;		 
		if(!isSuccess) {
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, clusterStatus);
	}
	
	public void modifyStart(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.MODIFY;		 
		if(!isSuccess) {
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, clusterStatus);
	}
	
	public void modifyFinish(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.MODIFY;		 
		if(!isSuccess) {
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, clusterStatus);
	}
	
	/**
	 * 클러스터 상태 업데이트
	 * @param clusterIdx
	 * @param mlClusterStatus
	 * @param clusterStatus
	 */
	private void setClusterStatus(Long clusterIdx, ClusterEntity.ProvisioningStatus clusterStatus) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);

		String now = DateUtil.currentDateTime();		
		clusterEntity.setProvisioningStatus(clusterStatus.name());		
		clusterEntity.setUpdatedAt(now);
		
		clusterDomainService.update(clusterEntity);
		
	}
    
    
    /**
     * 클라우드 공급자에 따른 Kubernetes 이름 반환.
     * @param cloudProvider
     * @return
     */
    private String getProvisiongType(String cloudProvider) {
		String provider = cloudProvider.toLowerCase();
		String type = null;
		if(provider.equals("azure")) {
			type = ProvisioningType.AKS.name();
		} else if(provider.equals("gcp")) {
			type = ProvisioningType.GKE.name();
		} else if(provider.equals("aws")) {
			type = ProvisioningType.EKS.name();
		} else if(provider.equals("naver")) {
			type = ProvisioningType.NAVER.name();
		}
		return type;
	}
    
    /**
	 * 클라우드 인터페이스와 메시지 교류를 위한 리퀘스트 토픽 이름을 불러온다.
	 * @return
	 */
	public String getCloudRequestTopic() {
		String cloudVender = mlSettingService.getCloudProvider();
		return getCloudRequestTopic(cloudVender);
	}
	
	public String getCloudRequestTopic(String provider) {
		String topicKey = String.format("plugin.kafka.topic.%s.request", provider.toLowerCase());
		return env.getProperty(topicKey);
	}
	
}
