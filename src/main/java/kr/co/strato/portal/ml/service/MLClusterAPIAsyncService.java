package kr.co.strato.portal.ml.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PortStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.ResourceRequirements;
import io.fabric8.kubernetes.api.model.Secret;
import io.fabric8.kubernetes.api.model.ServicePort;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.batch.v1.Job;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import kr.co.strato.adapter.cloud.common.service.AbstractDefaultParamProvider;
import kr.co.strato.adapter.cloud.common.service.CloudAdapterService;
import kr.co.strato.adapter.k8s.node.service.NodeAdapterService;
import kr.co.strato.adapter.k8s.secret.service.SecretAdapterService;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.adapter.ml.model.ForecastDto;
import kr.co.strato.adapter.ml.model.PodSpecDto;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.portal.cluster.model.ArgoCDInfo;
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
	
	@Autowired
	private SecretAdapterService secretAdapterService;
	
	@Autowired
	private NodeAdapterService nodeAdapterService;
	
	private KubernetesClient client;
	
	
	public String getPrometheusUrl(Long clusterIdx) {	
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		String externalUrl = getExternalUrl(cluster);		
		if(externalUrl == null) {
			log.error("Get Prometheus url fail. clusterIdx: {}", clusterIdx);
			log.error("External url is null.");
		}
		
		String url = String.format("http://%s/prometheus/graph", externalUrl);
		return url;
	}
	
	public String getGrafanaUrl(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		String externalUrl = getExternalUrl(cluster);
		if(externalUrl == null) {
			log.error("Get Grafana url fail. clusterIdx: {}", clusterIdx);
			log.error("External url is null.");
		}
		
		String url = String.format("http://%s/grafana", externalUrl);
		return url;
	}
	
	public String getGrafanaIframeUrl(Long clusterIdx) {
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		String externalUrl = getExternalUrl(cluster);
		if(externalUrl == null) {
			log.error("Get Grafana url fail. clusterIdx: {}", clusterIdx);
			log.error("External url is null.");
			return null;
		}
		String clusterMonitoringUrl = String.format("http://%s/grafana/d/4b545447f/cluster-monitoring?orgId=1&refresh=30s&theme=light&kiosk=tvm", externalUrl);clusterMonitoringUrl = String.format("http://%s/grafana/d/4b545447f/cluster-monitoring?orgId=1&refresh=30s&theme=light&kiosk=tvm", externalUrl);
		return clusterMonitoringUrl;
	}
	
	public ArgoCDInfo getArgoCDInfo(Long clusterIdx) {	
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = cluster.getClusterId();
		
		String externalUrl = getExternalUrl(cluster, "https");
		if(externalUrl == null) {
			log.error("Get ArgoCD url fail. clusterIdx: {}", clusterIdx);
			log.error("External url is null.");
		}
		
		String url = String.format("https://%s/argocd", externalUrl);		
		String password = null;
		try {
			Secret secret = secretAdapterService.get(kubeConfigId, "argocd", "argocd-initial-admin-secret");
			if(secret != null && secret.getData() != null && secret.getData().containsKey("password")) {
				String encodedPassword = secret.getData().get("password");
				password = base64Decoding(encodedPassword);
			} else {
				log.error("Argocd 패스워드가 존재하지 않습니다. clusterIdx: {}", clusterIdx);
			}
		} catch (Exception e) {
			log.error("Argocd 패스워드 가져오기 실패.", e);
		}		
		return ArgoCDInfo.builder().url(url).password(password).build();
	}
	
	public String getExternalUrl(ClusterEntity cluster) {		
		if(cluster.getProvider().toLowerCase().equals("kubernetes")) {
			//Private Cloud			
			return getPrivateExternalUrl(cluster);
			
		} else {
			//Public Cloud
			return getPublicExternalUrl(cluster);
		}
	}
	
	public String getExternalUrl(ClusterEntity cluster, String protocol) {		
		if(cluster.getProvider().toLowerCase().equals("kubernetes")) {
			//Private Cloud			
			return getPrivateExternalUrl(cluster, protocol);
			
		} else {
			//Public Cloud
			return getPublicExternalUrl(cluster);
		}
	}
	
	public String getPrivateExternalUrl(ClusterEntity cluster) {
		return getPrivateExternalUrl(cluster, "http");
	}
	
	public String getPrivateExternalUrl(ClusterEntity cluster, String protocol) {
		String externalUrl = null;
		Long kubeConfigId = cluster.getClusterId();
		
		io.fabric8.kubernetes.api.model.Service svc = getIngressService(kubeConfigId);
		if(svc != null) {
			ServicePort servicePort = null;
			
			Optional<ServicePort> op = svc.getSpec().getPorts().stream()
					.filter(p -> p.getNodePort() != null && p.getAppProtocol().equals(protocol))
					.findFirst();
			if(op.isPresent()) {
				servicePort = op.get();
			}
			
			if(servicePort != null) {				
				String p = servicePort.getProtocol();
				Integer nodePort = servicePort.getNodePort();				
				List<String> workerIps = nodeAdapterService.getWorkerNodeIps(kubeConfigId);
				for(String ip : workerIps) {
					String end = String.format("%s:%d", ip, nodePort);
					externalUrl = end;
					break;
				}
			}
		}
		return externalUrl;
	}
	
	public String getPublicExternalUrl(ClusterEntity cluster) {
		return getPublicExternalUrl(cluster, "http");
	}
	
	public String getPublicExternalUrl(ClusterEntity cluster, String protocol) {
		String externalUrl = null;
		Long kubeConfigId = cluster.getClusterId();
		
		io.fabric8.kubernetes.api.model.Service s = getIngressService(kubeConfigId);

		List<LoadBalancerIngress> list = s.getStatus().getLoadBalancer().getIngress();
		if(list != null && list.size() > 0) {
			LoadBalancerIngress loadBalancerIngres = list.get(0);
			
			PortStatus port = null;
			List<PortStatus> ports = loadBalancerIngres.getPorts();
			if(ports != null) {
				Optional<PortStatus> opt = ports.stream()
						.filter(p -> p.getProtocol().toLowerCase().equals(protocol)).findFirst();
				
				if(opt.isPresent()) {
					port = opt.get();
				}
			}
			
			
			externalUrl = loadBalancerIngres.getIp();
			if(externalUrl == null || externalUrl.isEmpty()) {
				externalUrl = loadBalancerIngres.getHostname();
			}
			
			if(port != null && port.getPort() != 80) {
				externalUrl = String.format("%s:%d", externalUrl, port.getPort());
			}
			
		}
		return externalUrl;
	}
	
	
	public io.fabric8.kubernetes.api.model.Service getIngressService(Long kubeConfigId) {
		io.fabric8.kubernetes.api.model.Service s 
				= serviceAdapterService.get(kubeConfigId, "ingress-nginx", "ingress-nginx-controller");
		return s;
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
		String clusterDesc = mlName + "를 수행하기 위한 클러스터.";
		String region = null;
		String kubeletVersion = null;
		
		//String instance = paasNodeSpec.getInstance();
		//int nodeCount = paasNodeSpec.getCount();
		
		String instance = null;
		int nodeCount = 2;
		
		//Provisioning Param 생성.
		AbstractDefaultParamProvider paramProvider = cloudAdapterService.getDefaultParamService(provider);
		Map<String, Object> provisioningParam = 
						paramProvider.genProvisioningParam(clusterName, clusterDesc, kubeletVersion, region, instance, nodeCount);
		
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
				
				//Default값 설정(0.5Core, 1GB, 10GB)
				//1Core
				Float defaultCpu = 0.5f;
				Float defaultMemory = 1.0f;
				Float defaultStorage = 10f;
				Float defaultGpu = 1f;
				Float defaultGpuMemory = 1f;
				
				float cpu = defaultCpu;
				float memory = defaultMemory;
				float storage = defaultStorage;
				
				
				ResourceRequirements resReq = c.getResources();
				if(resReq != null) {
					Map<String, Quantity> request = resReq.getRequests();
					Map<String, Quantity> limits = resReq.getLimits();
					
					Quantity rCpu = request.get("cpu");
					Quantity rMemory = request.get("memory");
					Quantity rStorage = request.get("ephemeral-storage");
					
					Quantity lCpu = limits.get("cpu");
					Quantity lMemory = limits.get("memory");
					Quantity lStorage = limits.get("ephemeral-storage");
					
					
					
					cpu = getCpuLimits(rCpu, lCpu, defaultCpu);
					memory = getMemStorageLimits(rMemory, lMemory, defaultMemory);
					storage = getMemStorageLimits(rStorage, lStorage, defaultStorage);
				}
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
	
	public String base64Decoding(String encodedString) {
		return base64Decoding(encodedString, "UTF-8");
	}
	
	public String base64Decoding(String encodedString, String charset) {
		Decoder decoder = Base64.getDecoder();
		byte[] decodedBytes1 = decoder.decode(encodedString.getBytes());
		String decodedString = null;
		try {
			decodedString = new String(decodedBytes1, charset);
		} catch (UnsupportedEncodingException e) {
			log.error("", e);
		}
		return decodedString;
	}
	
	
	private KubernetesClient getKubeClient() {
		if(client == null) {
			client = new DefaultKubernetesClient();
		}
		return client;
	}
}
