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
import kr.co.strato.adapter.ml.model.CreateArg;
import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.NodeSpecDto;
import kr.co.strato.adapter.ml.model.PodSpecDto;
import kr.co.strato.adapter.ml.service.AIAdapterService;
import kr.co.strato.adapter.ml.service.CloudAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.service.MLClusterDomainService;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.service.ClusterSyncService;
import kr.co.strato.portal.ml.model.MLClusterDto;
import kr.co.strato.portal.ml.model.MLClusterDtoMapper;
import kr.co.strato.portal.ml.model.MLClusterType;
import kr.co.strato.portal.setting.service.MLSettingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLClusterAPIService {
	
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
	
	private KubernetesClient client;

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
		log.info("Job cluster provisioning start");
		
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		List<PodSpecDto> podSpecs = getHWSpec(yaml);
		
		log.info("Pod Spec:");
		log.info( gson.toJson(podSpecs));
		
		String cloudVender = mlSettingService.getCloudVender();
		
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
				.provisioningStatus(ClusterEntity.ProvisioningStatus.STARTED.name())
				.createUserId("ml@strato.co.kr")
				.createUserName("ML관리자")
				.build();		
		clusterDomainService.register(clusterEntity);
		
			
		
		MLClusterEntity mlClusterEntity = MLClusterEntity.builder()
				.cluster(clusterEntity)
				.clusterType(MLClusterType.JOB_CLUSTER.getType())
				.createdAt(now)
				.updatedAt(now)
				.status(MLClusterEntity.ClusterStatus.PROVISIONING.name())
				.build();		
		mlClusterDomainService.save(mlClusterEntity);
		
		
		/*
		NodePool nodepool = NodePool.builder()
				.vmType(instance)
				.nodeCount(nodeCount)
				.build();
		List<NodePool> nodepools = new ArrayList<>();
		nodepools.add(nodepool);
		*/
		
		CreateArg createParam = CreateArg.builder()
				.clusterName(clusterName)
				.vmType(instance)
				.kubernetesVersion(kubeletVersion)
				.region(region)
				.vmType(instance)
				.nodeCount(nodeCount)
//				.nodePools(nodepools)
				.build();
		
		String createParamJson = gson.toJson(createParam);
		log.info("Request Param - Provisioning cluster");
		log.info(createParamJson);
				
		//Cluster provisioning		
		String kubeConfig = cloudAdapterService.provisioning(cloudVender, createParam);
		
		if(kubeConfig != null && kubeConfig.length() > 0) {			
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
				log.error("", e);
			}	
			
		} else {
			log.info("Job cluster provisioning fail.");
			log.error("kubeConfig is null.");
		}
		
		return mlClusterEntity;
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
			
			
			log.info("Delete cluster: {}, Provider: {}", clusterName, clusterEntity.getProvider());
			boolean isDelete = false;
			try {
				//클러스터 삭제
				isDelete = cloudAdapterService.delete(clusterEntity.getProvider(), clusterName);
			} catch (Exception e) {
				log.error("", e);
			}
			if(isDelete) {
				log.info("Delete cluster finish: {}, Provider: {}", clusterName, clusterEntity.getProvider());
			} else {
				log.info("Delete cluster fail: {}, Provider: {}", clusterName, clusterEntity.getProvider());
			}
			
			//실제 클러스터 삭제.
			//clusterDomainService.delete(clusterEntity);
			
			entity.setStatus(MLClusterEntity.ClusterStatus.DELETED.name());
			
			//삭제 완료로 DB 업데이트
			mlClusterDomainService.deleteByMlClusterIdx(mlClusterIdx);
		}
		
	}
	
	private KubernetesClient getKubeClient() {
		if(client == null) {
			client = new DefaultKubernetesClient();
		}
		return client;
	}
}
