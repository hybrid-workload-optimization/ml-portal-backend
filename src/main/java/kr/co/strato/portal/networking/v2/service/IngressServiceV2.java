package kr.co.strato.portal.networking.v2.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.PortStatus;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressPath;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressRuleValue;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackend;
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule;
import io.fabric8.kubernetes.api.model.networking.v1.IngressServiceBackend;
import io.fabric8.kubernetes.api.model.networking.v1.ServiceBackendPort;
import kr.co.strato.adapter.k8s.ingress.service.IngressAdapterService;
import kr.co.strato.adapter.k8s.ingressController.model.ServicePort;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.IngressController.service.IngressControllerDomainService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.portal.cluster.v1.service.ClusterNodeService;
import kr.co.strato.portal.networking.v1.model.IngressControllerDto;
import kr.co.strato.portal.networking.v1.model.IngressControllerDtoMapper;
import kr.co.strato.portal.networking.v2.model.IngressDto;
import kr.co.strato.portal.networking.v2.model.IngressDto.IngressRuleDto;
import kr.co.strato.portal.networking.v2.model.NetworkCommonDto;
import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import kr.co.strato.portal.workload.v2.service.WorkloadCommonV2;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IngressServiceV2 extends WorkloadCommonV2 {

	@Autowired
	private IngressAdapterService ingressAdapterService;
	
	@Autowired
	private IngressControllerDomainService ingressControllerDomainService;
	
	@Autowired
	private ClusterNodeService clusterNodeService;

	@Autowired
	private ClusterDomainService clusterDomainService;
	
	@Autowired
	private ServiceAdapterService serviceAdapterService;

	/**
	 * 리스트 조회
	 * @param clusterIdx
	 * @return
	 */
	public List<IngressDto> getList(Long clusterIdx) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
		
		List<Ingress> list = ingressAdapterService.getIngressList(kubeConfigId);
		List<IngressDto> result = new ArrayList<>();
		for(Ingress i : list) {
			IngressDto dto = (IngressDto) toDto(clusterEntity, i);
			result.add(dto);
		}
		return result;
	}
	
	/**
	 * 상세 조회
	 * @param search
	 * @return
	 */
	public IngressDto getDetail(NetworkCommonDto.Search search) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		
		Ingress ingrss = ingressAdapterService.get(kubeConfigId, search.getNamespace(), search.getName());
		IngressDto dto = (IngressDto) toDto(clusterEntity, ingrss);
		return dto;
	}

	/**
	 * 삭제
	 * @param search
	 * @return
	 */
	public boolean delete(NetworkCommonDto.Search search) throws Exception {
		ClusterEntity clusterEntity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		
		boolean isDeleted = ingressAdapterService.deleteIngress(kubeConfigId, search.getName(), search.getNamespace());
		return isDeleted;
	}

	
	/**
	 * Yaml 조회
	 * @param search
	 * @return
	 */
	public String getYaml(NetworkCommonDto.Search search) {
		ClusterEntity clusterEntity = clusterDomainService.get(search.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		
		String yaml = ingressAdapterService.getIngressYaml(kubeConfigId, search.getName(), search.getNamespace());
		yaml = Base64Util.encode(yaml);
		return yaml;
	}
	
	
	public IngressControllerEntity getIngressController(ClusterEntity cluster, String ingressClass) {
		
		IngressControllerEntity ingressController = null;
		if(ingressClass.equals("default")) {
			ingressController = ingressControllerDomainService.getDefaultController(cluster);
		} else {
			ingressController = ingressControllerDomainService.getIngressController(cluster, ingressClass);
		}
		
		if(ingressController != null) {
			ingressController.setCluster(cluster);		
		}
		return ingressController;
	}
	
	
	
	public io.fabric8.kubernetes.api.model.Service getIngressService(Long kubeConfigId) {
		io.fabric8.kubernetes.api.model.Service s 
				= serviceAdapterService.get(kubeConfigId, "ingress-nginx", "ingress-nginx-controller");
		return s;
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
						.filter(p -> p.getProtocol().toLowerCase().equals(protocol))
						.findFirst();
				
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
	
	
	

	@Override
	public WorkloadCommonDto toDto(ClusterEntity clusterEntity, HasMetadata data) throws Exception {
		IngressDto dto = new IngressDto();
		setMetadataInfo(data, dto);
		
		Ingress i = (Ingress) data;
		
		String ingressClass = i.getSpec().getIngressClassName();
		List<IngressRuleDto> ingressRuleList = new ArrayList<>();
		List<IngressRule> rules = i.getSpec().getRules();
		for (IngressRule rule : rules) {

			String host = rule.getHost();
			HTTPIngressRuleValue ruleValue = rule.getHttp();

			if (ruleValue != null) {
				List<HTTPIngressPath> rulePaths = ruleValue.getPaths();
				for (HTTPIngressPath rulePath : rulePaths) {
					String path = rulePath.getPath();
					String pathType = rulePath.getPathType();
					String protocol = "http";

					IngressBackend backend = rulePath.getBackend();
					IngressServiceBackend serviceBackend = backend.getService();
					String serviceName = serviceBackend.getName();
					ServiceBackendPort servicebackendPort = serviceBackend.getPort();

					Integer portNumber = servicebackendPort.getNumber();

					List<String> endpoints = endpoints(protocol, host, path, i, clusterEntity);

					
					IngressRuleDto ingressRuleEntity = IngressRuleDto.builder()
							.host(host)
							.protocol(protocol)
							.path(path)
							.pathType(pathType)
							.service(serviceName)
							.port(portNumber)
							.endpoints(endpoints)
							.build();
					
					ingressRuleList.add(ingressRuleEntity);
				}
			}
		}
		
		dto.setIngressClass(ingressClass);
		dto.setRuleList(ingressRuleList);
		return dto;
	}
	
	private List<String> endpoints(String protocol, String host, String path, Ingress ingress, ClusterEntity clusterEntity) {		
		List<String> endpoints = new ArrayList<>();
		
		//엔드포인트 조회 후 아이피 넣어야함.
		String ingressClass = ingress.getSpec().getIngressClassName();
		IngressControllerEntity ingressController = getIngressController(clusterEntity, ingressClass);
		
		if(ingressController != null) {
			String ingressControllerName = ingressController.getName();
			if(ingressControllerName.equals(IngressEntity.INGRESS_NAME_NGINX)) {
				IngressControllerDto.ResListDto  dto = IngressControllerDtoMapper.INSTANCE.toResListDto(ingressController);
				
				//ingressController가 존재하는 경우에만 Endpoint가 존재함.
				//ingress 생성 후 ingressController를 설치한 경우라면 해당 ingress를 업데이트 해야 동작함.
				
				String serviceType = dto.getServiceType();
				if(serviceType.equals(IngressControllerEntity.SERVICE_TYPE_NODE_PORT)) {
					//Node Port
					if(host != null && !host.equals("-")) {
						//host가 지정된 경우.
						List<ServicePort> ports = dto.getPort();
						for(ServicePort port : ports) {
							String prot = port.getProtocol();
							Integer p = port.getPort();
							
							String endpoint = String.format("%s://%s:%d%s", prot, host, p, path);
							endpoints.add(endpoint);
						}
					} else {
						//ip							
						Long clusterIdx = clusterEntity.getClusterIdx();																
						//List<String> workerIps = clusterNodeService.getWorkerNodeIps(clusterIdx);
						List<String> workerIps = clusterNodeService.getWorkerNodeIps(clusterIdx);
						
						List<ServicePort> ports = dto.getPort();
						for(ServicePort port : ports) {
							String prot = port.getProtocol();
							Integer p = port.getPort();
							
							for(String ip: workerIps) {
								String endpoint = String.format("%s://%s:%d%s", prot, ip, p, path);
								endpoints.add(endpoint);
							}
						}
					}
				} else if(serviceType.equals(IngressControllerEntity.SERVICE_TYPE_EXTERNAL_IPS)) {
					//ExternalIps					
					if(host != null) {
						String endpoint = String.format("%s://%s%s", protocol, host, path);
						endpoints.add(endpoint);
					} else {
						List<String> ips = dto.getExternalIp();
						for(String ip: ips) {
							String endpoint = String.format("%s://%s%s", protocol, ip, path);
							endpoints.add(endpoint);
						}
					}
					
				} else if(serviceType.equals(IngressControllerEntity.SERVICE_TYPE_LOAD_BALANCER)) {
					//Public 작업 해야함
					String externalUrl = getPublicExternalUrl(clusterEntity, protocol);
					String endpoint = String.format("%s://%s%s", protocol, externalUrl, path);
					endpoints.add(endpoint);
				}
			} else {
				log.error("Unknown ingress type: {}", ingressControllerName);
			}
		} else {
			//클러스터에 Ingress Controller가 설치 되지 않은 경우
			//Endpoint는 존재하지 않음.
		}
		return endpoints;
	}

}
