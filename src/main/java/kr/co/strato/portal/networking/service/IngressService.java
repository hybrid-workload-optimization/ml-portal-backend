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
	
	
	public Page<IngressDto> getIngressList(String name,NamespaceEntity namespace,Pageable pageable) {
		Page<IngressEntity> ingressPage = ingressDomainService.findByName(name,namespace,pageable);
		List<IngressDto> ingressList = ingressPage.getContent().stream().map(c -> IngressDtoMapper.INSTANCE.toDto(c)).collect(Collectors.toList());
		
		Page<IngressDto> page = new PageImpl<>(ingressList, pageable, ingressPage.getTotalElements());
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
				IngressEntity ingress = toEntity(i,clusterId);

				// save
				Long id = ingressDomainService.register(ingress);
				ids.add(id);
			} catch (JsonProcessingException e) {
				e.printStackTrace();
				throw new InternalServerException("parsing error");
			}
		}

		return ids;
	}
	
	
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteIngress(Long id,Long clusterId){
    	IngressEntity i = ingressDomainService.getDetail(id.longValue());
        String IngressName = i.getName();

        boolean isDeleted = ingressAdapterService.deleteIngress(clusterId, IngressName);
        if(isDeleted){
        	ingressRuleDomainService.delete(id);
            return ingressDomainService.delete(id.longValue());
        }else{
            throw new InternalServerException("k8s Ingress 삭제 실패");
        }
    }

	
    public IngressDto getIngressDetail(Long id){
    	IngressEntity ingressEntity = ingressDomainService.getDetail(id); 

    	IngressDto ingressDto = IngressDtoMapper.INSTANCE.toDto(ingressEntity);
        return ingressDto;
    }
	
	
    public String getIngressYaml(Long kubeConfigId,String name,String namespace){
     	String ingressYaml = ingressAdapterService.getIngressYaml(kubeConfigId,name,namespace); 
         return ingressYaml;
     }
    
	
	public List<Long> registerIngress(YamlApplyParam yamlApplyParam, Long clusterId) {
		String yamlDecode = Base64Util.decode(yamlApplyParam.getYaml());
		
		List<Ingress> ingressList = ingressAdapterService.registerIngress(yamlApplyParam.getKubeConfigId(), yamlDecode);
		List<Long> ids = new ArrayList<>();

		for (Ingress i : ingressList) {
			try {
				// k8s Object -> Entity
				IngressEntity ingress = toEntity(i,clusterId);
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
	
	public List<Long> updateIngress(Long ingressId,Long clusterId, YamlApplyParam yamlApplyParam){
        String yaml = Base64Util.decode(yamlApplyParam.getYaml());

        List<Ingress> ingress = ingressAdapterService.registerIngress(clusterId, yaml);

        List<Long> ids = ingress.stream().map( i -> {
            try {
            	IngressEntity updateIngress = toEntity(i,clusterId);

                Long id = ingressDomainService.update(updateIngress, ingressId, clusterId);
                
                ingressRuleDomainService.delete(id);
                //ingress rule save
				ingressRuleRegister(i,id);

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
	
	
	 private IngressEntity toEntity(Ingress i, Long clusterId) throws JsonProcessingException {
	    	// k8s Object -> Entity
			String name = i.getMetadata().getName();
			String uid = i.getMetadata().getUid();
			String ingressClass = i.getSpec().getIngressClassName();
			String createdAt = i.getMetadata().getCreationTimestamp();
			
			IngressEntity ingress = IngressEntity.builder().name(name).uid(uid)
					.ingressClass(ingressClass)
					.createdAt(DateUtil.strToLocalDateTime(createdAt))
					.build();

	        return ingress;
	    }
	 
	 
	 
	 
		public void ingressRuleRegister(Ingress i, Long ingressId) {
			List<IngressRuleEntity> ingressRuls = new ArrayList<>();
			List<IngressRule> rules = i.getSpec().getRules();
			for (IngressRule rule : rules) {

				String host = rule.getHost();
				HTTPIngressRuleValue ruleValue = rule.getHttp();

				if (host == null) {
					

				} else {
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

						IngressRuleEntity ingressRuleEntity = IngressRuleEntity.builder().host(host).protocol(protocol)
								.path(path).pathType(pathType).service(serviceName).port(portNumber).build();

						ingressRuls.add(ingressRuleEntity);
					}
				}

			}
			ingressRuleDomainService.saveAllingress(ingressRuls);

		}
		
	
}
