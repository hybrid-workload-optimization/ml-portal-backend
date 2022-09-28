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
import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import kr.co.strato.adapter.k8s.cluster.service.ClusterAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.ml.model.CloudParamDto;
import kr.co.strato.adapter.ml.model.CreateArg;
import kr.co.strato.adapter.ml.model.CreateArg.NodePool;
import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.PodSpecDto;
import kr.co.strato.adapter.ml.service.AIAdapterService;
import kr.co.strato.adapter.ml.service.CloudAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity.ProvisioningStatus;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.service.MLClusterDomainService;
import kr.co.strato.domain.machineLearning.service.MLClusterMappingDomainService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.service.ClusterSyncService;
import kr.co.strato.portal.ml.model.MLClusterDto;
import kr.co.strato.portal.ml.model.MLClusterDtoMapper;
import kr.co.strato.portal.ml.model.MLClusterType;
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
	private MLClusterDomainService mlClusterDomainService;
	
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
	WorkJobService workJobService;
	
	@Autowired
	private KafkaProducerService kafkaProducerService;
	
	private KubernetesClient client;
	
	@Autowired
	private MLClusterMappingDomainService mlClusterMappingDomainService;
	
	
	@Autowired
	private Environment env;
	
	

	/**
	 * Service Cluster 리스트 반환.
	 * @param pageRequest
	 * @return
	 */
	public List<MLClusterDto.List> getServiceClusterList() {
		List<MLClusterEntity> list = mlClusterDomainService.getList(MLClusterType.SERVICE_CLUSTER.getType());
		
		List<MLClusterDto.List> result = new ArrayList<>(); 
		for(MLClusterEntity entity : list) {
			MLClusterDto.List l = MLClusterDtoMapper.INSTANCE.toListDto(entity);
			result.add(l);
		}
		return result;
	}
	
	public MLClusterDto.Detail getServiceClusterDetail(Long clusterId) {
		MLClusterEntity entity = mlClusterDomainService.get(clusterId);
		if(entity != null) {
			MLClusterDto.Detail d = MLClusterDtoMapper.INSTANCE.toDetailDto(entity);
			return d;
		}
		return null;
	}
	
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
	public MLClusterEntity provisioningJobCluster(String mlName, String yaml) {
		log.info("[Provisioning Cluster]  작업 시작");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<PodSpecDto> podSpecs = getHWSpec(yaml);
		
		log.info("Pod Spec:");
		log.info( gson.toJson(podSpecs));
		
		String cloudVender = mlSettingService.getCloudProvider();
		
		//노드 구성 추천
		ForecastDto.ReqForecastDto param = ForecastDto.ReqForecastDto.builder()
				.model(cloudVender.toLowerCase())
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
		String region = "koreacentral";
		String kubeletVersion = "1.23.5";
		
		//String instance = paasNodeSpec.getInstance();
		//int nodeCount = paasNodeSpec.getCount();
		
		String instance = "Standard_DS2_v2";
		int nodeCount = 2;
		
		
		String now = DateUtil.currentDateTime();
		ClusterEntity clusterEntity = ClusterEntity.builder()
				.clusterName(clusterName)
				.description(mlName + "을 수행하기 위한 클러스터.")
				.createdAt(now)
				.provider(cloudVender)
				.providerVersion(kubeletVersion)
				.provisioningStatus(ClusterEntity.ProvisioningStatus.PENDING.name())
				.createUserId("ml@strato.co.kr")
				.createUserName("ML관리자")
				.build();		
		clusterDomainService.register(clusterEntity);
		
			
		
		MLClusterEntity mlClusterEntity = MLClusterEntity.builder()
				.cluster(clusterEntity)
				.clusterType(MLClusterType.JOB_CLUSTER.getType())
				.createdAt(now)
				.updatedAt(now)
				.status(MLClusterEntity.ClusterStatus.PENDING.name())
				.build();		
		mlClusterDomainService.save(mlClusterEntity);
		
	
		
		NodePool nodepool = NodePool.builder()
				.vmType(instance)
				.nodeCount(nodeCount)
				.build();
		List<NodePool> nodepools = new ArrayList<>();
		nodepools.add(nodepool);
		
		
		CreateArg createParam = CreateArg.builder()
				.clusterName(clusterName)
				.vmType(instance)
				.kubernetesVersion(kubeletVersion)
				.region(region)
				.vmType(instance)
				.nodeCount(nodeCount)
				.nodePools(nodepools)
				.build();
		
		String createParamJson = gson.toJson(createParam);
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
				.param(createParam)
				.build();
		
		String messageDataJson = gson.toJson(messageData);
		log.info("[Provisioning Cluster]  메세지 큐 등록");
		log.info(messageDataJson);
		
		//kafka 전송
		kafkaProducerService.sendMessage(getCloudRequestTopic(), messageDataJson);
		
		return mlClusterEntity;
	}
	
	public boolean finishJobCluster(MLClusterEntity mlClusterEntity, String kubeConfig) {
		ClusterEntity clusterEntity = mlClusterEntity.getCluster();
		String cloudVender = mlSettingService.getCloudProvider();
		
		try {
			ClusterAdapterDto clusterAdapterDto = ClusterAdapterDto.builder()
					.provider(cloudVender)
					.configContents(Base64.getEncoder().encodeToString(kubeConfig.getBytes()))
					.build();
			
			String strClusterId = clusterAdapterService.registerCluster(clusterAdapterDto);
			
			if (StringUtils.isEmpty(strClusterId)) {
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FAILED.name());
				mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.FAILED.name());
			} else {
				Long kubeConfigId = Long.valueOf(strClusterId);
				clusterEntity.setClusterId(kubeConfigId);
				clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
				mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.PROVISIONING_FINISHED.name());
				
				
				log.info("Cluster Synchronization started.");
				clusterSyncService.syncCluster(kubeConfigId, clusterEntity.getClusterIdx());
				log.info("Cluster Synchronization finished.");
			}
			
			clusterDomainService.update(clusterEntity);
			mlClusterDomainService.save(mlClusterEntity);
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
		return String.format("%s_%d", mlName, random);
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
	
	public void deleteMlCluster(Long mlClusterIdx) {
		log.info("[Delete Cluster]  작업 시작");
		MLClusterEntity entity = mlClusterDomainService.get(mlClusterIdx);
		if(entity != null) {
			ClusterEntity clusterEntity = entity.getCluster();
			
			String now = DateUtil.currentDateTime();
			entity.setStatus(MLClusterEntity.ClusterStatus.DELETING.name());
			entity.setUpdatedAt(now);
			clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.DELETING.name());
			clusterEntity.setUpdatedAt(now);
			
			clusterDomainService.update(clusterEntity);
			mlClusterDomainService.save(entity);
			
			String clusterName = entity.getCluster().getClusterName();
			
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
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_DELETE)
					.param(clusterName)
					.build();
			
			String messageDataJson = gson.toJson(messageData);
			log.info("[Delete Cluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(), messageDataJson);
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
		Long mlClusterIdx = scaleDto.getClusterId();
		MLClusterEntity entity = mlClusterDomainService.get(mlClusterIdx);
		
		if(entity != null) {
			String clusterName = entity.getCluster().getClusterName();
			Integer nodeCount = scaleDto.getNodeCount();
						
			//WorkJob 등록
			WorkJobDto workJobDto = WorkJobDto.builder().
					workJobTarget(clusterName)
					.workJobType(WorkJobType.CLUSTER_SCALE)
					.workJobStatus(WorkJobStatus.WAITING)
					.workJobStartAt(DateUtil.currentDateTime())
					.workSyncYn("N")
					.workJobReferenceIdx(entity.getCluster().getClusterIdx())
					.createUserId("ml@strato.co.kr")
					.createUserName("ML관리자")
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[Scale Cluster] work job idx : {}", workJobIdx);
			
			CloudParamDto.ScaleArg arg = new CloudParamDto.ScaleArg();
			arg.setClusterName(clusterName);
			arg.setNodeCount(nodeCount);
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_SCALE)
					.param(arg)
					.build();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String messageDataJson = gson.toJson(messageData);
			log.info("[Scale Cluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(), messageDataJson);
			
		} else {
			log.error("[Scale Cluster] 작업 실패!");
			log.error("[Scale Cluster] ML Cluster를 찾을 수 없습니다. mlClusterIdx: {}", mlClusterIdx);
		}
		
	}
	
	/**
	 * 노드풀 변경 작업.
	 * @param modifyDto
	 */
	public void modifyJobCluster(ModifyArgDto modifyDto) {
		log.info("[Modify Cluster]  작업 시작");
		Long mlClusterIdx = modifyDto.getClusterId();
		MLClusterEntity entity = mlClusterDomainService.get(mlClusterIdx);
		
		if(entity != null) {
			String clusterName = entity.getCluster().getClusterName();
			
			String vmType = modifyDto.getVmType();
			Integer nodeCount = modifyDto.getNodeCount();
						
			//WorkJob 등록
			WorkJobDto workJobDto = WorkJobDto.builder().
					workJobTarget(clusterName)
					.workJobType(WorkJobType.CLUSTER_MODIFY)
					.workJobStatus(WorkJobStatus.WAITING)
					.workJobStartAt(DateUtil.currentDateTime())
					.workSyncYn("N")
					.workJobReferenceIdx(entity.getCluster().getClusterIdx())
					.createUserId("ml@strato.co.kr")
					.createUserName("ML관리자")
					.build();
			
			Long workJobIdx = workJobService.registerWorkJob(workJobDto);
			log.info("[Modify Cluster] work job idx : {}", workJobIdx);
			
			CloudParamDto.ModifyArg arg = new CloudParamDto.ModifyArg();
			arg.setClusterName(clusterName);
			arg.setVmType(vmType);
			arg.setNodeCount(nodeCount);
			
			//kafka에 넣기 위한 데이터.
			MessageData messageData = MessageData.builder()
					.workJobIdx(workJobIdx)
					.jobType(MessageData.JOB_TYPE_MODIFY)
					.param(arg)
					.build();
			
			Gson gson = new GsonBuilder().setPrettyPrinting().create();
			String messageDataJson = gson.toJson(messageData);
			log.info("[Modify Cluster]  메세지 큐 등록");
			log.info(messageDataJson);
			
			//kafka 전송
			kafkaProducerService.sendMessage(getCloudRequestTopic(), messageDataJson);
			
		} else {
			log.error("[Modify Cluster] 작업 실패!");
			log.error("[Modify Cluster] ML Cluster를 찾을 수 없습니다. mlClusterIdx: {}", mlClusterIdx);
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
			MLClusterEntity.ClusterStatus clusterStartus = MLClusterEntity.ClusterStatus.PROVISIONING_STARTED;
			String now = DateUtil.currentDateTime();
			if(!isSuccess) {
				status = ProvisioningStatus.FAILED;
				clusterStartus = MLClusterEntity.ClusterStatus.PROVISIONING_FAIL;
			}			
			cluster.setProvisioningStatus(clusterStartus.name());
			cluster.setUpdatedAt(now);
			clusterDomainService.update(cluster);
			
			
			MLClusterEntity mlClusterEntity = mlClusterDomainService.get(cluster);
			mlClusterEntity.setStatus(clusterStartus.name());
			mlClusterEntity.setUpdatedAt(now);
			mlClusterDomainService.save(mlClusterEntity);
		}
		
		
	}
	
	/**
	 * 클러스터 프로비저닝 종료 콜백
	 * @param clusterIdx
	 * @param isSuccess
	 * @param data
	 */
	public MLClusterEntity provisioningFinish(Long clusterIdx, boolean isSuccess, Object data) {
		log.info("Callback process - provisioning finish");
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		MLClusterEntity mlClusterEntity = mlClusterDomainService.get(clusterEntity);
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
						mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.PROVISIONING_FAIL.name());
						
						log.error("KubeConfig 등록 실패");
					} else {
						Long kubeConfigId = Long.valueOf(strClusterId);
						clusterEntity.setClusterId(kubeConfigId);
						clusterEntity.setProvisioningStatus(ClusterEntity.ProvisioningStatus.FINISHED.name());
						mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.PROVISIONING_FINISHED.name());
						
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
				mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.PROVISIONING_FAIL.name());
				
				log.error("Cluster 생성 실패 했거나 KubeConfig 데이터가 잘못 되었습니다.");
				log.error("data: {}", data);
			}
		} else {			
			log.error("Cluster가 존재하지 않습니다.");
			log.error("clusterIdx: {}", clusterIdx);
		}
		
		String now = DateUtil.currentDateTime();
		clusterEntity.setUpdatedAt(now);
		mlClusterEntity.setUpdatedAt(now);
		
		clusterDomainService.update(clusterEntity);
		mlClusterDomainService.save(mlClusterEntity);
		log.info("Job cluster provisioning success.");
		return mlClusterEntity;
	}
	
	/**
	 * 클러스터 삭제 시작
	 * @param clusterIdx
	 * @param isSuccess
	 * @param data
	 */
	public void deleteStart(Long clusterIdx, boolean isSuccess, Object data) {
		MLClusterEntity.ClusterStatus mlClusterStatus = MLClusterEntity.ClusterStatus.DELETE_START;
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.DELETING;
		 
		if(!isSuccess) {
			mlClusterStatus = MLClusterEntity.ClusterStatus.DELETE_FAIL;
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, mlClusterStatus, clusterStatus);
	}
	
	public MLClusterEntity deleteFinish(Long clusterIdx, boolean isSuccess, Object data) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		MLClusterEntity mlClusterEntity = mlClusterDomainService.get(clusterEntity);
		
		String clusterName = clusterEntity.getClusterName();
		
		if(isSuccess) {
			mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.DELETED.name());			
			log.info("Delete cluster finish: {}, Provider: {}", clusterName, clusterEntity.getProvider());
		} else {
			mlClusterEntity.setStatus(MLClusterEntity.ClusterStatus.DELETE_FAIL.name());	
			log.info("Delete cluster fail: {}, Provider: {}", clusterName, clusterEntity.getProvider());
		}
		
		Long mlClusterIdx = mlClusterEntity.getId();
		
		//클러스터 삭제.
		mlClusterDomainService.deleteByMlClusterIdx(mlClusterIdx);
		return mlClusterEntity;
	}
	
	public void scaleStart(Long clusterIdx, boolean isSuccess, Object data) {		
		MLClusterEntity.ClusterStatus mlClusterStatus = MLClusterEntity.ClusterStatus.SCALE_START;
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.SCALE;		 
		if(!isSuccess) {
			mlClusterStatus = MLClusterEntity.ClusterStatus.SCALE_FAIL;
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, mlClusterStatus, clusterStatus);
	}
	
	public void scaleFinish(Long clusterIdx, boolean isSuccess, Object data) {	
		MLClusterEntity.ClusterStatus mlClusterStatus = MLClusterEntity.ClusterStatus.PROVISIONING_FINISHED;
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.FINISHED;		 
		if(!isSuccess) {
			mlClusterStatus = MLClusterEntity.ClusterStatus.SCALE_FAIL;
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, mlClusterStatus, clusterStatus);
	}
	
	public void modifyStart(Long clusterIdx, boolean isSuccess, Object data) {
		MLClusterEntity.ClusterStatus mlClusterStatus = MLClusterEntity.ClusterStatus.MODIFY_START;
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.MODIFY;		 
		if(!isSuccess) {
			mlClusterStatus = MLClusterEntity.ClusterStatus.MODIFY_FAIL;
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, mlClusterStatus, clusterStatus);
	}
	
	public void modifyFinish(Long clusterIdx, boolean isSuccess, Object data) {
		MLClusterEntity.ClusterStatus mlClusterStatus = MLClusterEntity.ClusterStatus.PROVISIONING_FINISHED;
		ClusterEntity.ProvisioningStatus clusterStatus = ClusterEntity.ProvisioningStatus.MODIFY;		 
		if(!isSuccess) {
			mlClusterStatus = MLClusterEntity.ClusterStatus.MODIFY_FAIL;
			clusterStatus = ClusterEntity.ProvisioningStatus.FAILED;
		}		
		setClusterStatus(clusterIdx, mlClusterStatus, clusterStatus);
	}
	
	/**
	 * 클러스터 상태 업데이트
	 * @param clusterIdx
	 * @param mlClusterStatus
	 * @param clusterStatus
	 */
	private void setClusterStatus(Long clusterIdx, MLClusterEntity.ClusterStatus mlClusterStatus, ClusterEntity.ProvisioningStatus clusterStatus) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		MLClusterEntity mlClusterEntity = mlClusterDomainService.get(clusterEntity);

		String now = DateUtil.currentDateTime();
		
		mlClusterEntity.setStatus(mlClusterStatus.name());
		clusterEntity.setProvisioningStatus(clusterStatus.name());
		
		mlClusterEntity.setUpdatedAt(now);
		clusterEntity.setUpdatedAt(now);
		
		clusterDomainService.update(clusterEntity);
		mlClusterDomainService.save(mlClusterEntity);
		
	}
	
	/**
	 * 클라우드 인터페이스와 메시지 교류를 위한 리퀘스트 토픽 이름을 불러온다.
	 * @return
	 */
	public String getCloudRequestTopic() {
		String cloudVender = mlSettingService.getCloudProvider().toLowerCase();
		String topicKey = String.format("plugin.kafka.topic.%s.request", cloudVender);
		return env.getProperty(topicKey);
	}
}
