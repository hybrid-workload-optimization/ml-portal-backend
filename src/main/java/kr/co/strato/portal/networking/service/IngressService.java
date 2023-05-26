package kr.co.strato.portal.networking.service;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.fabric8.kubernetes.api.model.LoadBalancerIngress;
import io.fabric8.kubernetes.api.model.PortStatus;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressPath;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressRuleValue;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackend;
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule;
import io.fabric8.kubernetes.api.model.networking.v1.IngressServiceBackend;
import io.fabric8.kubernetes.api.model.networking.v1.IngressSpec;
import io.fabric8.kubernetes.api.model.networking.v1.ServiceBackendPort;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.utils.Serialization;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.ingress.service.IngressAdapterService;
import kr.co.strato.adapter.k8s.ingressController.model.ServicePort;
import kr.co.strato.adapter.k8s.service.service.ServiceAdapterService;
import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.IngressController.service.IngressControllerDomainService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;
import kr.co.strato.domain.ingress.service.IngressDomainService;
import kr.co.strato.domain.ingress.service.IngressRuleDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.DuplicateIngressPathException;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.service.ClusterNodeService;
import kr.co.strato.portal.common.service.InNamespaceService;
import kr.co.strato.portal.common.service.ProjectAuthorityService;
import kr.co.strato.portal.ml.service.MLClusterAPIAsyncService;
import kr.co.strato.portal.networking.model.IngressControllerDto;
import kr.co.strato.portal.networking.model.IngressControllerDtoMapper;
import kr.co.strato.portal.networking.model.IngressDto;
import kr.co.strato.portal.networking.model.IngressDtoMapper;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IngressService extends InNamespaceService {

	@Autowired
	private IngressAdapterService ingressAdapterService;

	@Autowired
	private IngressDomainService ingressDomainService;

	@Autowired
	private IngressRuleDomainService ingressRuleDomainService;
	
	@Autowired
	private IngressControllerDomainService ingressControllerDomainService;
	
	@Autowired
	private ClusterNodeService clusterNodeService;

	@Autowired
	private ClusterDomainService clusterDomainService;

	@Autowired
	ProjectDomainService projectDomainService;
	
	@Autowired
	ProjectAuthorityService projectAuthorityService;
	
	@Autowired
	private ServiceAdapterService serviceAdapterService;
	
	@Autowired
	private MLClusterAPIAsyncService mlClusterService;

	public Page<IngressDto.ResListDto> getIngressList(Pageable pageable, IngressDto.SearchParam searchParam) {
		Page<IngressEntity> ingressPage = ingressDomainService.getIngressList(pageable, searchParam.getClusterIdx(),
				searchParam.getNamespaceIdx());
		List<IngressDto.ResListDto> ingressList = ingressPage.getContent().stream()
				.map(c -> IngressDtoMapper.INSTANCE.toResListDto(c)).collect(Collectors.toList());

		Page<IngressDto.ResListDto> page = new PageImpl<>(ingressList, pageable, ingressPage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<Ingress> getIngressListSet(Long clusterId) {
		List<Ingress> ingressList = ingressAdapterService.getIngressList(clusterId);

		synIngressSave(ingressList, clusterId);
		return ingressList;
	}

	public List<Long> synIngressSave(List<Ingress> ingressList, Long clusterId) {
		List<Long> ids = new ArrayList<>();
		for (Ingress i : ingressList) {
			try {
				IngressEntity ingress = toEntity(i, clusterId, 44L);

				// save
				Long id = ingressDomainService.register(ingress);
				// ingress rule save
				ingressRuleRegister(i, id);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}

	@Transactional(rollbackFor = Exception.class)
	public boolean deleteIngress(Long id) {
		IngressEntity i = ingressDomainService.getDetail(id.longValue());
		String IngressName = i.getName();
		String namespace = i.getNamespace().getName();
		Long clusterId = i.getNamespace().getCluster().getClusterId();

		boolean isDeleted = ingressAdapterService.deleteIngress(clusterId, IngressName, namespace);
		//if (isDeleted) {
			return ingressDomainService.delete(id.longValue());
		//} else {
		//	throw new InternalServerException("k8s Ingress 삭제 실패");
		//}
	}

	public IngressDto.ResDetailDto getIngressDetail(Long id, UserDto loginUser) {
		IngressEntity ingressEntity = ingressDomainService.getDetail(id);
		List<IngressRuleEntity> ruleList = ingressRuleDomainService.findByIngressId(id);

		Long clusterIdx = ingressEntity.getNamespace().getCluster().getClusterIdx();
		ClusterEntity cluster = clusterDomainService.get(clusterIdx);
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		Long projectIdx = projectEntity.getId();

		// 메뉴 접근권한 채크.
		projectAuthorityService.chechAuthority(getMenuCode(), projectIdx, loginUser);
		
		IngressDto.ResDetailDto ingressDto = IngressDtoMapper.INSTANCE.toResDetailDto(ingressEntity);
		List<IngressDto.RuleList> ruleDto = ruleList.stream().map(c -> IngressDtoMapper.INSTANCE.toRuleListDto(c))
				.collect(Collectors.toList());
		
		for(IngressDto.RuleList l : ruleDto) {
			String protocol = l.getProtocol();
			String host = l.getHost();
			String path = l.getPath();
			
			String externalUrl = null;
			
			//KB SKS 데모를 위한 코드
			if(cluster.getClusterName().equals("vsphere-cluster-demo")) {
				externalUrl = "10.10.20.180:30007";
			} else {
				externalUrl = mlClusterService.getExternalUrl(ingressEntity.getCluster(), protocol);
			}
			
			if(externalUrl != null && externalUrl.length() > 0) {
				String endpoint = protocol + "://" + externalUrl + path;
				l.setEndpoints(Arrays.asList(endpoint));
			}
		}
		
		ingressDto.setRuleList(ruleDto);
		ingressDto.setProjectIdx(projectIdx);
		return ingressDto;
	}

	public String getIngressYaml(Long kubeConfigId, String name, String namespace) {
		String yaml = ingressAdapterService.getIngressYaml(kubeConfigId, name, namespace);
		yaml = Base64Util.encode(yaml);
		return yaml;
	}
	
	
	public String getYaml(Long serviceId){
		IngressEntity eEntity = ingressDomainService.get(serviceId);
        String yaml = eEntity.getYaml();
        
        if(yaml == null) {
        	 String name = eEntity.getName();
             String namespaceName = eEntity.getNamespace().getName();
             Long clusterId = eEntity.getNamespace().getCluster().getClusterId();

             yaml = ingressAdapterService.getIngressYaml(clusterId, namespaceName, name);
        }
        yaml = Base64Util.encode(yaml);
        return yaml;
    }
	

	public List<Long> registerIngress(IngressDto.ReqCreateDto yamlApplyParam) {
		Long clusterIdx = yamlApplyParam.getKubeConfigId();
		
		//이름 중복 채크
		duplicateCheckResourceCreation(clusterIdx, yamlApplyParam.getYaml());
		
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		
		
		
		//ingress path 중복 채크
		boolean isDuplicateIngressPath = duplicateCheckIngressPath(clusterIdx, yamlDecode);
		if(isDuplicateIngressPath) {
			log.error("중복된 Ingress path 입니다.");
			throw new DuplicateIngressPathException();
		}
		
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long clusterId = clusterEntity.getClusterId();
		List<Ingress> ingressList = ingressAdapterService.registerIngress(clusterId, yamlDecode);
		List<Long> ids = new ArrayList<>();

		for (Ingress i : ingressList) {
			try {
				// k8s Object -> Entity
				IngressEntity ingress = toEntity(i, clusterId, clusterIdx);
				ingress.setCluster(clusterEntity);
				ingress.setYaml(yamlDecode);
				
				// save
				Long id = ingressDomainService.register(ingress);

				// ingress rule save
				ingressRuleRegister(i, id);

				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	/**
	 * Ingress path 중복 채크
	 * @param yamlDecode
	 * @return
	 */
	private boolean duplicateCheckIngressPath(Long clusterIdx, String yamlDecode) {
		KubernetesClient client = getClient();
		
		//base64 decoding
		InputStream is = new ByteArrayInputStream(yamlDecode.getBytes());
		List<HasMetadata> ress = client.load(is).get();
		
		List<String> paths = new ArrayList<>();
		for(HasMetadata data : ress) {
			if(data instanceof Ingress) {
				Ingress ingress = (Ingress) data;
				
				String namespace = ingress.getMetadata().getNamespace();
				if(namespace == null) {
					namespace = "default";
				}
				
				
				
				IngressSpec spec = ingress.getSpec();
				List<IngressRule> rules = spec.getRules();
				for(IngressRule rule: rules) {
					HTTPIngressRuleValue ruleValue = rule.getHttp();

					
					List<HTTPIngressPath> rulePaths = ruleValue.getPaths();
					for(HTTPIngressPath rulePath : rulePaths) {
						String path = rulePath.getPath();
						
						if(paths.contains(path)) {
							//이미 중복 발생.
							return true;
						} else {
							paths.add(path);
						}
					}
				}
			}
		}
		
		if(paths.size() > 0) {
			return ingressRuleDomainService.duplicateCheckIngressPath(clusterIdx, paths);
		}
		return false;
	}

	public List<Long> updateIngress(Long ingressId, YamlApplyParam yamlApplyParam) {
		String yaml = Base64Util.decode(yamlApplyParam.getYaml());
		
		ClusterEntity clusterEntity = clusterDomainService.get(yamlApplyParam.getKubeConfigId());
		Long clusterId = clusterEntity.getClusterId();
		Long clusterIdx = clusterEntity.getClusterIdx();
		
		/*
		//ingress path 중복 채크
		boolean isDuplicateIngressPath = duplicateCheckIngressPath(clusterIdx, yaml);
		if(isDuplicateIngressPath) {
			log.error("중복된 Ingress path 입니다.");
			throw new DuplicateIngressPathException();
		}
		*/

		List<Ingress> ingress = ingressAdapterService.registerIngress(clusterId, yaml);

		List<Long> ids = ingress.stream().map(i -> {
			try {
				IngressEntity updateIngress = toEntity(i, clusterId, clusterIdx);
				updateIngress.setCluster(clusterEntity);
				updateIngress.setYaml(yaml);

				Long id = ingressDomainService.update(updateIngress, ingressId);

				boolean ruleDel = ingressRuleDomainService.delete(id);

				if (ruleDel) {
					// ingress rule save
					ingressRuleRegister(i, ingressId);
				}
				return id;
			} catch (JsonProcessingException e) {
				log.error(e.getMessage(), e);
				throw new InternalServerException("json 파싱 에러");
			} catch (Exception e) {
				log.error(e.getMessage(), e);
				throw new InternalServerException("Ingress update error");
			}
		}).collect(Collectors.toList());
		return ids;
	}	

	private IngressEntity toEntity(Ingress i, Long clusterId, Long clusterIdx) throws JsonProcessingException {
		// k8s Object -> Entity
		String name = i.getMetadata().getName();
		String uid = i.getMetadata().getUid();
		String ingressClass = i.getSpec().getIngressClassName();
		String createdAt = i.getMetadata().getCreationTimestamp();

		//IngressControllerEntity ingressControllerEntity = new IngressControllerEntity();

		NamespaceEntity namespaceEntity = ingressDomainService.findByName(i.getMetadata().getNamespace(), clusterIdx);
		if(ingressClass == null) {
			ingressClass = "default";
		}
		
		

		IngressEntity ingress = IngressEntity.builder()
				.name(name)
				.uid(uid)
				.ingressClass(ingressClass)
				.createdAt(DateUtil.strToLocalDateTime(createdAt))
				.namespace(namespaceEntity).build();
		return ingress;
	}

	public void ingressRuleRegister(Ingress i, Long ingressIdx) {
		IngressEntity ingress = ingressDomainService.get(ingressIdx);
		List<IngressRuleEntity> ingressRuls = new ArrayList<>();
		List<IngressRule> rules = i.getSpec().getRules();
		for (IngressRule rule : rules) {

			String host = rule.getHost();
			HTTPIngressRuleValue ruleValue = rule.getHttp();

			if (ruleValue != null) {
				ObjectMapper mapper = new ObjectMapper();
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

					List<String> endpoints = endpoints(protocol, host, path, ingress);				
					String endpointStr = null;
					try {
						endpointStr = mapper.writeValueAsString(endpoints);
					} catch (JsonProcessingException e) {
						e.printStackTrace();
					}

					IngressRuleEntity ingressRuleEntity = IngressRuleEntity.builder()
							.ingress(ingress)
							.host(host)
							.protocol(protocol)
							.path(path)
							.pathType(pathType)
							.service(serviceName)
							.port(portNumber)
							.endpoint(endpointStr)
							.build();
					
					
					
					ingressRuls.add(ingressRuleEntity);
				}
				ingressRuleDomainService.saveAllingress(ingressRuls);
			}
		}
	}
	
	
	public IngressControllerEntity getIngressController(IngressEntity ingress) {
		ClusterEntity cluster = ingress.getNamespace().getCluster();
		String ingressClass = ingress.getIngressClass();
		
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
	
	/**
	 * endpoint 리스트 반환.
	 * @param protocol
	 * @param host
	 * @param portNumber
	 * @param path
	 * @return
	 */
	private List<String> endpoints(String protocol, String host, String path, IngressEntity ingress) {		
		List<String> endpoints = new ArrayList<>();
		
		//엔드포인트 조회 후 아이피 넣어야함.
		IngressControllerEntity ingressController = getIngressController(ingress);
		
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
						Long clusterIdx = ingress.getNamespace().getCluster().getClusterIdx();																
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
					String externalUrl = getPublicExternalUrl(ingress.getNamespace().getCluster(), protocol);
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
	
	/**
	 * IngressController에 소속된 모든 ingress 룰 업데이트
	 * IngressController 생성, 수정, 삭제 only
	 * @param ingressControllerEntity
	 * @throws Exception
	 */
	public void updateIngressRule(IngressControllerEntity ingressControllerEntity) {
		List<IngressEntity> list = ingressDomainService.getIngressByIngressController(ingressControllerEntity);
		if(list != null) {
			list.forEach(i -> {
				try {
					updateIngressRule(i);
				} catch (Exception e) {
					log.error("", e);
				}
			});
		}
	}
	
	
	/**
	 * Ingress Rule 업데이트
	 * IngressController 생성, 수정, 삭제 only
	 * @param ingressEntity
	 * @throws Exception
	 */
	public void updateIngressRule(IngressEntity ingressEntity) throws Exception {
		Long id = ingressEntity.getId();
		Long kubeConfigId = ingressEntity.getCluster().getClusterId();
		String namespace = ingressEntity.getNamespace().getName();
		String name = ingressEntity.getName();
		
		//기존 룰 삭제
		boolean ruleDel = ingressRuleDomainService.delete(id);
		if (ruleDel) {
			Ingress ingress = null;
			try {
				ingress = ingressAdapterService.get(kubeConfigId, namespace, name);
				
				//ingressController 반영을 위해 ingress update.			
				String yaml =  Serialization.asYaml(ingress);
				ingressAdapterService.registerIngress(kubeConfigId, yaml);
			} catch(Exception e) {
				log.error("", e);
			}
			// ingress rule save
			ingressRuleRegister(ingress, id);
		}
	}

	@Override
	protected InNamespaceDomainService getDomainService() {
		return ingressDomainService;
	}

}
