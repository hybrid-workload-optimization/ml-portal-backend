package kr.co.strato.portal.ml.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;
import kr.co.strato.adapter.cloud.common.service.CloudAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.PodSpecDto;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.cluster.model.ModifyArgDto;
import kr.co.strato.portal.cluster.model.PublicClusterDto;
import kr.co.strato.portal.cluster.model.ScaleArgDto;
import kr.co.strato.portal.cluster.service.PublicClusterService;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.service.MLSettingService;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class MLClusterAPIAsyncService {
	
	@Autowired
	private CloudAdapterService cloudAdapterService;
	
	@Autowired
	private MLSettingService mlSettingService;
	
	@Autowired
	private PublicClusterService publicClusterService;
	
	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private ServiceAdapterService serviceAdapterService;
	
	private KubernetesClient client;
	
	
	public String getPrometheusUrl(Long clusterIdx) {		
		String url = null;
		
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		String cloudProvider = cluster.getProvider().toLowerCase();

		if(cloudProvider.equals("azure")) {
			String externalIp = getAzureIngressExternalIp(cluster.getClusterId());			
			url = String.format("http://%s/prometheus/graph", externalIp);
		}
		
		if(url == null) {
			log.error("Get Prometheus url fail.");
			log.error("Unknown cloud provider: {}", cloudProvider);
		}
		
		return url;
	}
	
	public String getGrafanaUrl(Long clusterIdx) {		
		String url = null;
		
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		String cloudProvider = cluster.getProvider().toLowerCase();

		if(cloudProvider.equals("azure")) {
			String externalIp = getAzureIngressExternalIp(cluster.getClusterId());
			url = String.format("http://%s/grafana", externalIp);
		}
		
		if(url == null) {
			log.error("Get Prometheus url fail.");
			log.error("Unknown cloud provider: {}", cloudProvider);
		}
		
		return url;
	}
	
	private String getAzureIngressExternalIp(Long kubeConfigId) {
		String externalIp = "";
		io.fabric8.kubernetes.api.model.Service s 
			= serviceAdapterService.get(kubeConfigId, "ingress-nginx", "ingress-nginx-controller");

		List<LoadBalancerIngress> list = s.getStatus().getLoadBalancer().getIngress();
		if(list != null && list.size() > 0) {
			LoadBalancerIngress loadBalancerIngres = list.get(0);
			externalIp = loadBalancerIngres.getIp();
		}
		return externalIp;
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
		
		//Provisioning Param 생성.
		AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(provider);
		Map<String, Object> provisioningParam = 
						paramProvider.genProvisioningParam(clusterName, kubeletVersion, region, instance, nodeCount);
		
		PublicClusterDto.Povisioning pParam = PublicClusterDto.Povisioning.builder()
				.cloudProvider(provider)
				.povisioningParam(provisioningParam)
				.build();
		
		UserDto user = UserDto.builder()
				.userId("ml@strato.co.kr")
				.userName("ML관리자")
				.build();
		
		ClusterEntity entity = publicClusterService.provisioningCluster(pParam, user);
		return entity;
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
		PublicClusterDto.Delete deleteParam = PublicClusterDto.Delete.builder()
				.clusterIdx(clusterIdx)
				.build();		
		
		UserDto user = UserDto.builder()
				.userId("ml@strato.co.kr")
				.userName("ML관리자")
				.build();		
		publicClusterService.deleteCluster(deleteParam, user);
	}
	
	public void scaleJobCluster(ScaleArgDto scaleDto) {
		UserDto user = UserDto.builder()
				.userId("ml@strato.co.kr")
				.userName("ML관리자")
				.build();		
		publicClusterService.scaleJobCluster(scaleDto, user);
	}
	
	public void modifyJobCluster(ModifyArgDto modifyDto) {
		UserDto user = UserDto.builder()
				.userId("ml@strato.co.kr")
				.userName("ML관리자")
				.build();		
		publicClusterService.modifyJobCluster(modifyDto, user);
	}
	
	
	private KubernetesClient getKubeClient() {
		if(client == null) {
			client = new DefaultKubernetesClient();
		}
		return client;
	}
}
