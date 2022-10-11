package kr.co.strato.portal.ml.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;
import kr.co.strato.adapter.cloud.common.service.CloudAdapterService;
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.PodSpecDto;
import kr.co.strato.adapter.ml.service.AIAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.service.ClusterSyncService;
import kr.co.strato.portal.ml.model.MessageData;
import kr.co.strato.portal.ml.model.ModifyArgDto;
import kr.co.strato.portal.ml.model.ScaleArgDto;
import kr.co.strato.portal.setting.service.MLSettingService;
import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import kr.co.strato.portal.work.model.WorkJobDto;
import kr.co.strato.portal.work.service.WorkJobService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLClusterAPIAsyncService {
	
	@Autowired
	private NodeAdapterService nodeAdapterService;
	
	@Autowired
	private AIAdapterService mlAdapterService;
	
	@Autowired
	private CloudAdapterService cloudAdapterService;
	
	@Autowired
	private MLSettingService mlSettingService;
	
	@Autowired
	private ClusterAdapterService clusterAdapterService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private ClusterSyncService clusterSyncService;
	
	@Autowired
	private WorkJobService workJobService;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	private KubernetesClient client;
	
	
	@Autowired
	private Environment env;
	
	
	public String getPrometheusUrl(Long clusterId) {
		/*
		MLClusterEntity entity = mlClusterDomainService.get(clusterId);
		
		String host = "external-ip";
		List<String> nodeIps = nodeAdapterService.getWorkerNodeIps(entity.getCluster().getClusterId());
		if(nodeIps != null && nodeIps.size() > 0) {
			host = nodeIps.get(0);
		}
		
		String url = String.format("http://%s:30005", host);
		*/
		String url = "http://210.217.178.114:30015/";
		return url;
	}	
	
	/**
	 * Job 클러스터 프로비저닝
	 * @param yaml
	 * @return
	 */
	public ClusterEntity provisioningJobCluster(String mlName, String yaml) {
		log.info("[Provisioning Cluster]  작업 시작");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<PodSpecDto> podSpecs = getHWSpec(yaml);
		
		log.info("Pod Spec:");
		log.info( gson.toJson(podSpecs));
		
		String provider = mlSettingService.getCloudProvider();
		
		//노드 구성 추천
		ForecastDto.ReqForecastDto param = ForecastDto.ReqForecastDto.builder()
				.model(provider.toLowerCase())
				.category("gpu")
				.podSpec(podSpecs)
				.build();
		/*
		ForecastDto.ResForecastDto recommandResult = mlAdapterService.forecast(param);
		
		//추천 노드
		List<NodeSpecDto> nodeSpecList = recommandResult.getItems();
		
		log.info("추천된 Node spec:");
		log.info( gson.toJson(nodeSpecList));
		
		NodeSpecDto paasNodeSpec = nodeSpecList.get(0);
		
		if(paasNodeSpec == null) {
			//노드 추천 실패!
			log.error("Cluster Node 추천 실패!!!");
			
		}
		
		log.info("[추천된 노드 상품]");
		log.info(gson.toJson(paasNodeSpec));
		*/
		
		//상품명
		//String instance = paasNodeSpec.getInstance();
		//int nodeCount = paasNodeSpec.getCount();
		
		String clusterName = genClusterName(mlName);		
		String region = null;
		String kubeletVersion = null;
		
		//String instance = paasNodeSpec.getInstance();
		//int nodeCount = paasNodeSpec.getCount();
		
		String instance = null;
		int nodeCount = 2;
		
		
		String now = DateUtil.currentDateTime();
		ClusterEntity clusterEntity = ClusterEntity.builder()
				.clusterName(clusterName)
				.description(mlName + "을 수행하기 위한 클러스터.")
				.createdAt(now)
				.provider(provider)
				.providerVersion(kubeletVersion)
				.provisioningStatus(ClusterEntity.ProvisioningStatus.PENDING.name())
				.createUserId("ml@strato.co.kr")
				.createUserName("ML관리자")
				.build();		
		clusterDomainService.register(clusterEntity);
		
	
		AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(provider);
		
		//Provisioning Param 생성.
		Map<String, Object> provisioningParam = 
				paramProvider.genProvisioningParam(clusterName, kubeletVersion, region, instance, nodeCount);
		
		//provisioningParam.put("nodePools", null);
		
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
				.createUserId("ml@strato.co.kr")
				.createUserName("ML관리자")
				.build();
		
		Long workJobIdx = workJobService.registerWorkJob(workJobDto);
		log.info("[Provisioning Cluster] work job idx : {}", workJobIdx);
		
		
		//kafka에 넣기 위한 데이터.
		MessageData messageData = MessageData.builder()
				.workJobIdx(workJobIdx)
				.jobType(MessageData.JOB_TYPE_PROVISIONING)
				.param(provisioningParam)
				.build();
		
		String messageDataJson = gson.toJson(messageData);
		log.info("[Provisioning Cluster]  메세지 큐 등록");
		log.info(messageDataJson);
		
		//kafka 전송
		kafkaProducerService.sendMessage(getCloudRequestTopic(), messageDataJson);
		
		return clusterEntity;
	}
	
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
	
	private String genClusterName(String mlName) {
		int min = 10000;
		int max = 99999;
		int random = (int) ((Math.random() * (max - min)) + min);
		return String.format("%s_%d", mlName, random).replace("_", "-");
	}
	
	
	/**
	 * Yaml 내용을 파싱하여 요구되는 H/W Spec를 구해 반환.
	 * @param yaml
	 * @return
	 */
	private List<PodSpecDto> getHWSpec(String yaml) {
		KubernetesClient client = getKubeClient();
		InputStream is = new ByteArrayInputStream(yaml.getBytes());
		List<HasMetadata> resoures = client.load(is).get();
		
		List<PodSpecDto> result = new ArrayList<>();
		for(HasMetadata data : resoures) {
			List<PodSpecDto> list = getHWSpec(data);
			result.addAll(list);
		}
		return result;
	}
	
	private List<PodSpecDto> getHWSpec(HasMetadata h) {
		PodSpec podSpec = null;
		if(h instanceof Deployment) {
			Deployment deployment  = (Deployment) h;
			podSpec = deployment.getSpec().getTemplate().getSpec();
		} else if(h instanceof Job) {
			Job job = (Job) h;
			podSpec = job.getSpec().getTemplate().getSpec();
		}
		
		List<PodSpecDto> list = new ArrayList<>();
		if(podSpec != null) {
			//String kind = h.getKind();
			//String resName = h.getMetadata().getName();
			
			List<Container> containers =  podSpec.getContainers();
			for(Container c : containers) {
				String containerName = c.getName();
				ResourceRequirements resReq = c.getResources();
				
				Map<String, Quantity> request = resReq.getRequests();
				Map<String, Quantity> limits = resReq.getLimits();
				
				Quantity rCpu = request.get("cpu");
				Quantity rMemory = request.get("memory");
				Quantity rStorage = request.get("ephemeral-storage");
				
				Quantity lCpu = limits.get("cpu");
				Quantity lMemory = limits.get("memory");
				Quantity lStorage = limits.get("ephemeral-storage");
				
				//Default값 설정(0.5Core, 1GB, 10GB)
				//1Core
				Float defaultCpu = 0.5f;
				Float defaultMemory = 1.0f;
				Float defaultStorage = 10f;
				Float defaultGpu = 1f;
				Float defaultGpuMemory = 1f;
				
				float cpu = getCpuLimits(rCpu, lCpu, defaultCpu);
				float memory = getMemStorageLimits(rMemory, lMemory, defaultMemory);
				float storage = getMemStorageLimits(rStorage, lStorage, defaultStorage);				
				
				PodSpecDto podSpecDto = PodSpecDto.builder()
						.name(containerName)
						.cpu(cpu)
						.memory(memory)
						.storage(storage)
						.gpu(defaultGpu)
						.gpuMemory(defaultGpuMemory)
						.build();
				list.add(podSpecDto);
			}
		}
		return list;
	}
	
	/**
	 * CPU 요구사항을 구해 반환 한다.
	 * limits이 존재하는 경우 limits의 값을 구해 리턴.
	 * limits이 null 이고 request가 존재하는 경우 디폴트 cpu 사양과 비교를 하여 request가 큰 경우 request * 2를 한다.
	 * request, limits이 null인 경우 디폴트 cpu 사양을 반환.
	 * @param request
	 * @param limits
	 * @return
	 */
	private float getCpuLimits(Quantity request, Quantity limits, float defaultValue) {
		//default cpu
		float cpu = defaultValue;
		if(limits != null) {
			BigDecimal bCpu = Quantity.getAmountInBytes(limits);
			cpu = bCpu.floatValue();
			
		} else {
			if(request != null) {
				//request cpu가 존재하는 경우 디폴트 cpu 값과 비교한다.
				BigDecimal bCpu = Quantity.getAmountInBytes(request);
				float reqCpu = bCpu.floatValue();
				if(cpu < reqCpu) {
					//디폴트 cpu 사양이 요구 사항보다 작은 경우 요구사항 *2를 한다.
					cpu = reqCpu * 2;
				}
			}
		}
		return cpu;
	}
	
	/**
	 * Memory, Storage 요구사항을 구해 GB 단위로 변환하여 반환 한다.
	 * limits이 존재하는 경우 limits의 값을 구해 리턴.
	 * limits이 null 이고 request가 존재하는 경우 디폴트 사양과 비교를 하여 request가 큰 경우 request * 2를 한다.
	 * request, limits이 null인 경우 디폴트 cpu 사양을 반환.
	 * @param request
	 * @param limits
	 * @return
	 */
	private float getMemStorageLimits(Quantity request, Quantity limits, float defaultValue) {
		//default cpu
		float value = defaultValue;
		if(limits != null) {
			BigDecimal bCpu = Quantity.getAmountInBytes(limits);
			value = bCpu.floatValue();
			
		} else {
			if(request != null) {
				//request cpu가 존재하는 경우 디폴트 cpu 값과 비교한다.
				BigDecimal bCpu = Quantity.getAmountInBytes(request);
				float reqCpu = bCpu.floatValue();
				if(value < reqCpu) {
					//디폴트 cpu 사양이 요구 사항보다 작은 경우 요구사항 *2를 한다.
					value = reqCpu * 2;
				}
			}
		}
		
		//GB 단위로 변경
		float i = (float)(value / (1024.0 * 1024.0 * 1024.0));
		return i;
	}
	
	public void deleteMlCluster(Long clusterIdx) {
		log.info("[Delete Cluster]  작업 시작");
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
					.createUserId("ml@strato.co.kr")
					.createUserName("ML관리자")
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[Delete Cluster] work job idx : {}", workJobIdx);
			
			String cloudProvider = clusterEntity.getProvider();
			AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(cloudProvider);
			
			Map<String, Object> param = paramProvider.genDeleteParam(clusterName, null);
			
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_DELETE)
					.param(param)
					.build();
			
			String messageDataJson = gson.toJson(messageData);
			log.info("[Delete Cluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(cloudProvider), messageDataJson);
		}
	}
	
	
	private KubernetesClient getKubeClient() {
		if(client == null) {
			client = new DefaultKubernetesClient();
		}
		return client;
	}
	
	/**
	 * Job 클러스터 프로비저닝
	 * @param yaml
	 * @return
	 */
	public void scaleJobCluster(ScaleArgDto scaleDto) {
		log.info("[Scale Cluster]  작업 시작");
		Long clusterIdx = scaleDto.getClusterId();
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
					.createUserId("ml@strato.co.kr")
					.createUserName("ML관리자")
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
	
	/**
	 * 노드풀 변경 작업.
	 * @param modifyDto
	 */
	public void modifyJobCluster(ModifyArgDto modifyDto) {
		log.info("[Modify Cluster]  작업 시작");
		Long clusterIdx = modifyDto.getClusterId();
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
					.createUserId("ml@strato.co.kr")
					.createUserName("ML관리자")
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
	
	public ClusterEntity deleteFinish(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		
		String clusterName = clusterEntity.getClusterName();
		
		if(isSuccess) {		
			log.info("Delete cluster finish: {}, Provider: {}", clusterName, clusterEntity.getProvider());
		} else {
			log.info("Delete cluster fail: {}, Provider: {}", clusterName, clusterEntity.getProvider());
		}
		
		//클러스터 삭제.
		return clusterEntity;
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
