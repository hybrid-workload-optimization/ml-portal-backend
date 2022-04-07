package kr.co.strato.portal.networking.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;

import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressPath;
import io.fabric8.kubernetes.api.model.networking.v1.HTTPIngressRuleValue;
import io.fabric8.kubernetes.api.model.networking.v1.Ingress;
import io.fabric8.kubernetes.api.model.networking.v1.IngressBackend;
import io.fabric8.kubernetes.api.model.networking.v1.IngressRule;
import io.fabric8.kubernetes.api.model.networking.v1.IngressServiceBackend;
import io.fabric8.kubernetes.api.model.networking.v1.ServiceBackendPort;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.adapter.k8s.ingress.service.IngressAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.ingress.model.IngressControllerEntity;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;
import kr.co.strato.domain.ingress.service.IngressDomainService;
import kr.co.strato.domain.ingress.service.IngressRuleDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.networking.model.IngressDto;
import kr.co.strato.portal.networking.model.IngressDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class IngressService {

	@Autowired
	private IngressAdapterService ingressAdapterService;
	
	@Autowired
	private IngressDomainService ingressDomainService;
	
	@Autowired
	private IngressRuleDomainService ingressRuleDomainService;
	
    @Autowired
    private ClusterDomainService clusterDomainService;
	
	
	public Page<IngressDto.ResListDto> getIngressList(Pageable pageable,IngressDto.SearchParam searchParam) {
		Page<IngressEntity> ingressPage = ingressDomainService.getIngressList(pageable,searchParam.getClusterIdx(),searchParam.getNamespaceIdx());
		List<IngressDto.ResListDto> ingressList = ingressPage.getContent().stream().map(c -> IngressDtoMapper.INSTANCE.toResListDto(c)).collect(Collectors.toList());
		
		Page<IngressDto.ResListDto> page = new PageImpl<>(ingressList, pageable, ingressPage.getTotalElements());
		return page;
	}

	@Transactional(rollbackFor = Exception.class)
	public List<Ingress> getIngressListSet(Long clusterId) {
		List<Ingress> ingressList = ingressAdapterService.getIngressList(clusterId);
		
		synIngressSave(ingressList,clusterId);
		return ingressList;
	}

	public List<Long> synIngressSave(List<Ingress> ingressList, Long clusterId) {
		List<Long> ids = new ArrayList<>();
		for (Ingress i : ingressList) {
			try {
				IngressEntity ingress = toEntity(i,clusterId,44L);

				// save
				Long id = ingressDomainService.register(ingress);
				//ingress rule save
				ingressRuleRegister(i,id);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteIngress(Long id){
    	IngressEntity i = ingressDomainService.getDetail(id.longValue());
        String IngressName = i.getName();
        String namespace = i.getNamespace().getName();
        Long clusterId = i.getNamespace().getCluster().getClusterId();
        
        boolean isDeleted = ingressAdapterService.deleteIngress(clusterId, IngressName,namespace);
		if (isDeleted) {
			return ingressDomainService.delete(id.longValue());
		} else {
			throw new InternalServerException("k8s Ingress 삭제 실패");
		}
    }

	
    public IngressDto.ResDetailDto getIngressDetail(Long id){
    	IngressEntity ingressEntity = ingressDomainService.getDetail(id); 
    	List<IngressRuleEntity> ruleList = ingressRuleDomainService.findByIngressId(id);

    	IngressDto.ResDetailDto ingressDto = IngressDtoMapper.INSTANCE.toResDetailDto(ingressEntity);
    	List<IngressDto.RuleList> ruleDto = ruleList.stream().map(c -> IngressDtoMapper.INSTANCE.toRuleListDto(c)).collect(Collectors.toList());
    	ingressDto.setRuleList(ruleDto);
        return ingressDto;
    }
	
	
	public String getIngressYaml(Long kubeConfigId, String name, String namespace) {
		String yaml = ingressAdapterService.getIngressYaml(kubeConfigId, name, namespace);
		yaml = Base64Util.encode(yaml);
		return yaml;
	}
    
	
	public List<Long> registerIngress(IngressDto.ReqCreateDto yamlApplyParam) {
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		ClusterEntity clusterEntity = clusterDomainService.get(yamlApplyParam.getKubeConfigId());
		Long clusterId = clusterEntity.getClusterId();
		Long clusterIdx = clusterEntity.getClusterIdx();
		List<Ingress> ingressList = ingressAdapterService.registerIngress(clusterId, yamlDecode);
		List<Long> ids = new ArrayList<>();

		for (Ingress i : ingressList) {
			try {
				// k8s Object -> Entity
				IngressEntity ingress = toEntity(i,clusterId,clusterIdx);
				// save
				Long id = ingressDomainService.register(ingress);
				
				//ingress rule save
				ingressRuleRegister(i,id);
				
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	public List<Long> updateIngress(Long ingressId, YamlApplyParam yamlApplyParam){
        String yaml = Base64Util.decode(yamlApplyParam.getYaml());
        
        ClusterEntity clusterEntity = clusterDomainService.get(yamlApplyParam.getKubeConfigId());
		Long clusterId = clusterEntity.getClusterId();
		Long clusterIdx = clusterEntity.getClusterIdx();
        
        List<Ingress> ingress = ingressAdapterService.registerIngress(clusterId, yaml);

        List<Long> ids = ingress.stream().map( i -> {
            try {
            	IngressEntity updateIngress = toEntity(i,clusterId,clusterIdx);

                Long id = ingressDomainService.update(updateIngress, ingressId);
                
                boolean ruleDel = ingressRuleDomainService.delete(id);
               
                if(ruleDel) {
                //ingress rule save
				ingressRuleRegister(i,id);
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
	
	 
	 private IngressEntity toEntity(Ingress i, Long clusterId,Long clusterIdx) throws JsonProcessingException {
	    	// k8s Object -> Entity
			String name = i.getMetadata().getName();
			String uid = i.getMetadata().getUid();
			String ingressClass = i.getSpec().getIngressClassName();
			String createdAt = i.getMetadata().getCreationTimestamp();
			
			IngressControllerEntity ingressControllerEntity = new IngressControllerEntity();
			
			NamespaceEntity namespaceEntity = ingressDomainService.findByName(i.getMetadata().getNamespace(),clusterIdx);
			if(ingressClass != null){
				
				List<Ingress> ingressClassK8s = ingressAdapterService.getIngressClassName(clusterId,ingressClass);
				for (Ingress ic : ingressClassK8s) {
					ingressClass= ic.getMetadata().getName();
				}
				
				ingressControllerEntity = ingressDomainService.findIngressControllerByName(ingressClass);
				
			}else {
				ingressControllerEntity = ingressDomainService.findByDefaultYn("Y");
			}

			IngressEntity ingress = IngressEntity.builder().name(name).uid(uid)
					.ingressClass(ingressClass)
					.ingressController(ingressControllerEntity)
					.createdAt(DateUtil.strToLocalDateTime(createdAt))
					.namespace(namespaceEntity)
					.build();

	        return ingress;
	    }
	 
	 
	 
		public void ingressRuleRegister(Ingress i, Long ingressId) {
			List<IngressRuleEntity> ingressRuls = new ArrayList<>();
			List<IngressRule> rules = i.getSpec().getRules();
			for (IngressRule rule : rules) {

				String host = rule.getHost();
				HTTPIngressRuleValue ruleValue = rule.getHttp();

				if (host != null) {

				} else {
					if(ruleValue!=null) {
						List<HTTPIngressPath> rulePaths = ruleValue.getPaths();
						for (HTTPIngressPath rulePath : rulePaths) {
							String path = rulePath.getPath();
							String pathType = rulePath.getPathType();
							String protocol = "http";

							if(host == null) {
								//빈값 임시 디폴트값
								IngressControllerEntity ingressControllerEntity = ingressDomainService.findByDefaultYn("Y");
								host = ingressControllerEntity.getAddress();
							}
							
							IngressBackend backend = rulePath.getBackend();
							IngressServiceBackend serviceBackend = backend.getService();
							String serviceName = serviceBackend.getName();
							ServiceBackendPort servicebackendPort = serviceBackend.getPort();

							Integer portNumber = servicebackendPort.getNumber();
							
							IngressEntity ingressEntity = new IngressEntity();
							ingressEntity.setId(ingressId);
							
							IngressRuleEntity ingressRuleEntity = IngressRuleEntity.builder().ingress(ingressEntity).host(host).protocol(protocol)
									.path(path).pathType(pathType).service(serviceName).port(portNumber).build();

							ingressRuls.add(ingressRuleEntity);
						}
						ingressRuleDomainService.saveAllingress(ingressRuls);

					}
					
				}
			}

		}
	
}
