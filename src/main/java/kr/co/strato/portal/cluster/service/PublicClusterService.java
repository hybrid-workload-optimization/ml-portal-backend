package kr.co.strato.portal.cluster.service;

import java.io.IOException;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.Node;
import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;
import kr.co.strato.adapter.cloud.common.service.CloudAdapterService;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.sso.model.dto.CSPAccountDTO;
import kr.co.strato.adapter.sso.service.CSPAccountAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningType;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.kubeconfig.model.KubeconfigEntity;
import kr.co.strato.domain.kubeconfig.service.KubeconfigDomainService;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.service.NodeDomainService;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.service.ProjectClusterDomainService;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.global.util.EncryptUtil;
import kr.co.strato.portal.addon.service.AddonService;
import kr.co.strato.portal.cluster.model.ModifyArgDto;
import kr.co.strato.portal.cluster.model.PublicClusterDto;
import kr.co.strato.portal.cluster.model.ScaleArgDto;
import kr.co.strato.portal.ml.model.MessageData;
import kr.co.strato.portal.networking.service.IngressControllerService;
import kr.co.strato.portal.plugin.service.KafkaProducerService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.service.MLSettingService;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.model.WorkJobDto;
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
	private NodeAdapterService nodeAdapterService;
	
	@Autowired
	private ClusterNodeService clusterNodeService;
	
	@Autowired
	private IngressControllerService ingressControllerService;
	
	@Autowired
	private AddonService addonService;
	
	@Autowired
	private Environment env;
	
	@Autowired
	private CSPAccountAdapterService cspAccountAdapterService;
	
	@Autowired
	private ProjectClusterDomainService projectClusterDomainService;
	
	@Autowired
	private KubeconfigDomainService kubeconfigDomainService;
	
	@Autowired
	private NodeDomainService nodeDomainService;
	
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
		String cloudProvider = getCloudProviderLabel(param.getCloudProvider());
		
		Map<String, Object> provisioningParam = param.getPovisioningParam();		
		
		boolean isVaild = true;
		AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
		if(paramProvider != null) {
			//isVaild = paramProvider.isVaildProvisioningParam(provisioningParam);
		} 
		if(!isVaild) {
			log.error("[ProvisioningCluster] - 클러스터 생성 실패!");
			log.error("[ProvisioningCluster] - 유효하지 않은 파라메터.");
			throw new BadRequestException("Invalid parameter.");
		}
		
		
		Long projectIdx = null;
		if(provisioningParam.get(AbstractDefaultParamProvider.PROJECT_IDX) instanceof String) {
			projectIdx = Long.parseLong((String)provisioningParam.get(AbstractDefaultParamProvider.PROJECT_IDX));
		} else {
			projectIdx = (Long) provisioningParam.get(AbstractDefaultParamProvider.PROJECT_IDX);
		}
		
		String clusterName = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_CLUSTER_NAME);
		String clusterDesc = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_CLUSTER_DESC);
		String region = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_REGION);
		String kubeletVersion = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_KUBERNETES_VERSION);
		String cspAccountUuid = (String) provisioningParam.get(AbstractDefaultParamProvider.KEY_CSP_ACCOUNT_UUID);
		
		String vmType =  null;
		
		if(provisioningParam.get(AbstractDefaultParamProvider.KEY_NODE_POOLS) != null) {
			vmType = (String)((Map)((List)provisioningParam.get(AbstractDefaultParamProvider.KEY_NODE_POOLS)).get(0)).get(AbstractDefaultParamProvider.KEY_VM_TYPE);
		}
		
		int nodeCount = 0;
		if(provisioningParam.get(AbstractDefaultParamProvider.KEY_NODE_POOLS) != null) {
			nodeCount = (int)((Map)((List)provisioningParam.get(AbstractDefaultParamProvider.KEY_NODE_POOLS)).get(0)).get(AbstractDefaultParamProvider.KEY_NODE_COUNT);
		} else if(provisioningParam.get("workerSpec") != null) {
			nodeCount = (int)(((Map)provisioningParam.get("workerSpec")).get(AbstractDefaultParamProvider.KEY_NODE_COUNT));
		}
		
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
				.cspAccountUuid(cspAccountUuid)
				.build();		
		clusterDomainService.register(clusterEntity);
		
		//서비스 그룹에 클러스터 추가
		if(projectIdx != null) {    		
    		ProjectClusterEntity projectClusterEntity = ProjectClusterEntity.builder()
    				.projectIdx(projectIdx)
    				.clusterIdx(clusterEntity.getClusterIdx())
    				.addedAt(now)
    				.build();
            projectClusterDomainService.createProjectCluster(projectClusterEntity);
		}
		
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
		
		
		//CSP 계정 설정
		if(cspAccountUuid != null) {
			header = getCSPAccountHeader(cspAccountUuid);
		}
		
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
    
    
    public String getCloudProviderLabel(String provider) {
    	String type = null;
    	String lowerProvider = provider.toLowerCase();
    	if(lowerProvider.equals("kubernetes")) {
			type = "Kubernetes";
		} else if(lowerProvider.equals("azure")) {
			type = "Azure";
		} else if(lowerProvider.equals("gcp")) {
			type = "GCP";
		} else if(lowerProvider.equals("aws")) {
			type = "AWS";
		} else if(lowerProvider.equals("naver")) {
			type = "Naver";
		} else if(lowerProvider.equals("vmware")) {
			type = "VMware";
		}
    	return type;
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
			String regin = clusterEntity.getRegion();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			
			//WorkJob 등록
			WorkJobDto workJobDto = WorkJobDto.builder().
					workJobTarget(clusterName)
					.workJobType(WorkJobType.CLUSTER_DELETE)
					.workJobStatus(WorkJobStatus.WAITING)
					.workJobStartAt(DateUtil.currentDateTime())
					.workSyncYn("N")
					.workJobReferenceIdx(clusterEntity.getClusterIdx())
					//.createUserId(user.getUserId())
					//.createUserName(user.getUserName())
					.callbackUrl(callbackUrl)
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[DeleteCluster] work job idx : {}", workJobIdx);
			
			//CSP 계정 설정
			String cspAccountUuid = clusterEntity.getCspAccountUuid();
			if(cspAccountUuid != null && header == null) {
				header = getCSPAccountHeader(cspAccountUuid);
			}
			
			String cloudProvider = clusterEntity.getProvider();
			AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
			Map<String, Object> param = paramProvider.genDeleteParam(clusterName, regin);
			
			
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
			
			int masterCount = 0;
			// node
			List<NodeEntity> nodes = nodeDomainService.getNodeList(clusterIdx);
			if(nodes != null) {
				List<NodeEntity> masterNodes = nodes.stream().filter(n -> n.getRole().contains("master")).collect(Collectors.toList());
				masterCount = masterNodes.size();
			}
			
			if(masterCount > 0) {
				nodeCount = nodeCount - masterCount;
			}
			log.info("masterCount: {}", masterCount);
			log.info("nodeCount: {}", nodeCount);
			
						
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
			
			//CSP 계정 설정
			String cspAccountUuid = entity.getCspAccountUuid();
			if(cspAccountUuid != null && header == null) {
				header = getCSPAccountHeader(cspAccountUuid);
			}
			
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
			
			//CSP 계정 설정
			String cspAccountUuid = entity.getCspAccountUuid();
			if(cspAccountUuid != null && header == null) {
				header = getCSPAccountHeader(cspAccountUuid);
			}
			
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
	
	private Map<String, Object> getCSPAccountHeader(String cspAccountUuid) {		
		Map<String, Object> header = new HashMap<>();
		CSPAccountDTO account = cspAccountAdapterService.getAccount(cspAccountUuid);
		Map<String, String> accountData = account.getAccountData();
		
		String primaryKey = (accountData != null) ? accountData.get("primaryKey") : null;
		String accessKey = (accountData != null) ? accountData.get("accessKey") : null;
		String secretKey = (accountData != null) ? accountData.get("secretKey") : null;
		
		if (primaryKey != null && accessKey != null && secretKey != null) {
			String keyString = EncryptUtil.decryptRSA(primaryKey);
			String[] keyArr = keyString.split(":");
			if (keyArr != null && keyArr.length == 2) {
				accessKey = EncryptUtil.decryptAES(keyArr[0], keyArr[1], accessKey);
				secretKey = EncryptUtil.decryptAES(keyArr[0], keyArr[1], secretKey);
				
				header.put("access_key", accessKey);
				header.put("access_secret", secretKey);
			}
		}
		return header;
	}

	
	/**
	 * 클러스터 프로비저닝 시작 콜백
	 * @param clusterIdx
	 * @param isSuccess
	 */
	public ClusterEntity provisioningStart(Long clusterIdx, boolean isSuccess, Object data) {
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
		return cluster;
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
		//String cloudVender = mlSettingService.getCloudProvider();
		if(clusterEntity != null) {
			if(isSuccess && data != null && data instanceof String) {
				String kubeConfig = (String) data;
				try {
					
					
					KubeconfigEntity entity = KubeconfigEntity.builder()
							.configContents(kubeConfig)
							.provider(clusterEntity.getProvider())
							.regDate(DateUtil.currentDateTime())
							.modDate(DateUtil.currentDateTime())
							.build();
					entity = kubeconfigDomainService.save(entity);
					
					Long kubeConfigId = entity.getKubeConfigId();
					clusterEntity.setClusterId(kubeConfigId);
					clusterDomainService.update(clusterEntity);
					
					
					log.info("KubeConfig 등록 완료");
					log.info("Cluster Synchronization started.");
					try {
						clusterSyncService.syncCluster(kubeConfigId, clusterEntity.getClusterIdx());
					} catch (Exception e) {
						log.error("", e);
						
					}						
					
					//모니터링 패키지 설치(데모를 위해 기본 설치 한다.)
					log.info("Install Monitoring Package started.");
					try {
						instalAddonPackage(clusterEntity);
					} catch (Exception e) {
						log.error("", e);
					}						
					log.info("Install Monitoring Package Finished.");
					
					//패키지 로딩까지 30초 대기
					try {
						Thread.sleep(30000);
					} catch (InterruptedException e1) {
						e1.printStackTrace();
					}
					
					
					String now = DateUtil.currentDateTime();
					clusterEntity.setUpdatedAt(now);
					clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
					clusterDomainService.update(clusterEntity);
					log.info("Job cluster provisioning success.");
					
					
					log.info("Cluster Synchronization finished.");
										
				} catch (Exception e) {
					clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
					clusterDomainService.update(clusterEntity);
					log.error("", e);
				}
				
				/*
				//싱가폴 시연을 위해 주석처리.
				//모니터링 패키지 설치(데모를 위해 기본 설치 한다.)
				Executors.newSingleThreadExecutor().execute(new Runnable() {
					
					@Override
					public void run() {
						log.info("Install Monitoring Package started.");
						try {
							installMonitoringPackage(clusterEntity);
						} catch (Exception e) {
							log.error("", e);
						}						
						log.info("Install Monitoring Package Finished.");
						
						
						log.info("Install ArgoCD Package started.");
						try {
							installArgoCDPackage(clusterEntity);
						} catch (Exception e) {
							log.error("", e);
						}						
						log.info("Install ArgoCD Package Finished.");
					}
				});
				*/
				
			} else {
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
				clusterDomainService.update(clusterEntity);
				log.error("Cluster 생성 실패 했거나 KubeConfig 데이터가 잘못 되었습니다.");
				log.error("data: {}", data);
			}
		} else {			
			log.error("Cluster가 존재하지 않습니다.");
			log.error("clusterIdx: {}", clusterIdx);
		}	
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
		
		//클러스터 카운트 업데이트
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		int nodeCount = 0;
		try {
			List<Node> list = nodeAdapterService.getNodeList(clusterEntity.getClusterId());
			nodeCount = list.size();
			
			//노드 테이블 동기화.
			clusterNodeService.synClusterNodeSave(list, clusterIdx);
		} catch (Exception e) {
			log.error("", e);
		}

		String now = DateUtil.currentDateTime();		
		clusterEntity.setProvisioningStatus(clusterStatus.name());
		clusterEntity.setNodeCount(nodeCount);
		clusterEntity.setUpdatedAt(now);
		
		clusterDomainService.update(clusterEntity);
		
		
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
			type = ProvisioningType.NKS.name();
		} else if(provider.equals("vmware")) {
			type = ProvisioningType.vSphere.name();
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
	
	
	
	/**
	 * 클러스터 생성 완료 후 ML
	 * @param mlClusterEntity
	 */
	public boolean instalAddonPackage(Long clusterIdx) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);		
		return instalAddonPackage(clusterEntity);
	}
	
	public boolean instalAddonPackage(ClusterEntity clusterEntity) {
		boolean isOk = true;
		
		Long clusterIdx = clusterEntity.getClusterIdx();
				
		//IngressController 생성.
		try {
			log.info("Ingress Controller 설치 시작. clusterIdx: {}", clusterIdx);
			ingressControllerService.create(clusterEntity);
			log.info("Ingress Controller 설치 종료. clusterIdx: {}", clusterIdx);
		} catch (IOException e) {
			log.info("Ingress Controller 설치 실패. clusterIdx: {}", clusterIdx);
			log.error("", e);
			
			isOk = false;
		}
		
		//IngressController 생성 완료까지 대기
		try {
			Thread.sleep(100000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		
		//Monitoring Addon 설치
		Map<String, Object> parameters = new HashMap<>();	
		try {
			log.info("Monitoring addon 설치 시작. clusterIdx: {}", clusterIdx);
			addonService.installAddon(clusterIdx, "1", parameters, null);
			log.info("Monitoring addon 설치 종료. clusterIdx: {}", clusterIdx);
		} catch (IOException e) {
			log.info("Monitoring addon 설치 실패. clusterIdx: {}", clusterIdx);
			log.error("", e);
			isOk = false;
		}
		
		
		//ArgoCD Addon 설치
		try {
			log.info("ArgoCD addon 설치 시작. clusterIdx: {}", clusterIdx);
			addonService.installAddon(clusterIdx, "2", parameters, null);
			log.info("ArgoCD addon 설치 종료. clusterIdx: {}", clusterIdx);
		} catch (IOException e) {
			log.info("ArgoCD addon 설치 실패. clusterIdx: {}", clusterIdx);
			log.error("", e);
			isOk = false;
		}		
		return isOk;
	}	
}
