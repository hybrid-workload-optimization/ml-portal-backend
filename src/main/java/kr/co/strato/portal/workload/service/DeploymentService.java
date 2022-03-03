package kr.co.strato.portal.workload.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import kr.co.strato.adapter.k8s.deployment.service.DeploymentAdapterService;
import kr.co.strato.portal.workload.model.DeploymentArgDto;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.DeploymentDtoMapper;
import lombok.extern.slf4j.Slf4j;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.repository.DeploymentRepository;
import kr.co.strato.domain.deployment.service.DeploymentDomainService;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.global.model.PageRequest;

@Slf4j
@Service
public class DeploymentService {
	@Autowired
	DeploymentDomainService deploymentDomainService;
	
	@Autowired
	DeploymentRepository deploymentRepository;
	
	@Autowired
	DeploymentAdapterService deploymentAdapterService;
	
	@Autowired
	NamespaceDomainService namespaceDomainService;
	
	
	//목록
	public Page<DeploymentDto> getList(PageRequest pageRequest, DeploymentArgDto args){
		Page<DeploymentEntity> entities=  deploymentRepository.getDeploymentPageList(pageRequest.of(), args);
		List<DeploymentDto> dtos = entities.getContent().stream().map(DeploymentDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		Page<DeploymentDto> result = new PageImpl<>(dtos, pageRequest.of(), entities.getTotalElements());
		return result;
	}
	
	//상세
	public DeploymentDto get(Long idx){
		DeploymentEntity entitiy = deploymentDomainService.getDeploymentEntitiy(idx);
		DeploymentDto dto = DeploymentDtoMapper.INSTANCE.toDto(entitiy);
		return dto;
	}
	
	//생성
	public void create(DeploymentArgDto deploymentArgDto){
		save(deploymentArgDto);
	}
	
	//수정
	public void update(DeploymentArgDto deploymentArgDto){
		save(deploymentArgDto);
	}
	
	private void save(DeploymentArgDto deploymentArgDto){
		Long clusterId = deploymentArgDto.getClusterId();
		String yaml = deploymentArgDto.getYaml();
		
		
		List<Deployment> deployments = deploymentAdapterService.create(clusterId, yaml);
		//deployment 저장.
		List<DeploymentEntity> eneities = deployments.stream().map(d -> {
			DeploymentEntity deploymentEntity = null;
			try {
				deploymentEntity = toEntity(d);
			} catch (JsonProcessingException e) {
				log.debug(yaml);
			}
			
			if(deploymentEntity != null) {
				NamespaceEntity namespaceEntity = new NamespaceEntity();
				namespaceEntity.setId(deploymentArgDto.getNamespaceIdx());
				deploymentEntity.setNamespaceEntity(namespaceEntity);
				
				//수정시
				if(deploymentArgDto.getDeploymentIdx() != null)
					deploymentEntity.setDeploymentIdx(deploymentArgDto.getDeploymentIdx());
				
				deploymentDomainService.save(deploymentEntity);
			}
			
			return deploymentEntity;
		}).collect(Collectors.toList());
		
		//TODO replicatset 저장.
		//TODO pod 저장.
	}
	
	
	//삭제
	public void delete(DeploymentArgDto deploymentArgDto){
		Long clusterId = deploymentArgDto.getClusterId();
		Long namespaceIdx = deploymentArgDto.getNamespaceIdx();
		Long deploymentIdx = deploymentArgDto.getDeploymentIdx();
		String deploymentName = null;
		String namespaceName = null;
		
		NamespaceEntity namespaceEntity = namespaceDomainService.getDetail(namespaceIdx);
		if(namespaceEntity != null)
			namespaceName = namespaceEntity.getName();
		
		DeploymentEntity deploymentEntity = deploymentDomainService.getDeploymentEntitiy(deploymentIdx);
		if(deploymentEntity != null)
			deploymentName = deploymentEntity.getDeploymentName();
		
		deploymentAdapterService.delete(clusterId, namespaceName, deploymentName);
		deploymentDomainService.delete(deploymentIdx);
	}
	
	
    private DeploymentEntity toEntity(Deployment d) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        
        String name = null;
        String uid = null;
        String annotation = null;
        String label = null;
        String strategy = null;
        String selector = null;
        String maxSurge = null;
        String maxUnavailable = null;
        Integer podUpdated = null;
        Integer podReplicas = null;
        Integer podReady = null;
        String condition = null;
        //TODO
        String image = null;
        
        io.fabric8.kubernetes.api.model.ObjectMeta metadata = d.getMetadata();
        if(metadata != null) {        
        	name = metadata.getName();
        	uid = metadata.getUid();
        	annotation = mapper.writeValueAsString(metadata.getAnnotations());
        	label = mapper.writeValueAsString(metadata.getLabels());
        }
        
        io.fabric8.kubernetes.api.model.apps.DeploymentSpec deploymentSpec = d.getSpec();
        if(deploymentSpec != null) {
        	io.fabric8.kubernetes.api.model.apps.DeploymentStrategy deploymentStrategy = deploymentSpec.getStrategy();
        	if(deploymentStrategy != null) {
        		//
        		strategy = deploymentStrategy.getType();
        		if("RollingUpdate".equals(strategy) && deploymentStrategy.getRollingUpdate() != null) {
        			//
        			maxSurge = deploymentStrategy.getRollingUpdate().getMaxSurge().getStrVal();
        			//
        			maxUnavailable = deploymentStrategy.getRollingUpdate().getMaxUnavailable().getStrVal();
        		}
        		
        	}
        	if(deploymentSpec.getSelector() != null)
        		//
        		selector = mapper.writeValueAsString(deploymentSpec.getSelector().getMatchLabels());
        }
        
        io.fabric8.kubernetes.api.model.apps.DeploymentStatus deploymentStatus = d.getStatus();
        if(deploymentStatus != null) {
        	podUpdated = deploymentStatus.getUpdatedReplicas();
        	podReplicas = deploymentStatus.getReplicas();
        	podReady = deploymentStatus.getReadyReplicas();
        	condition = mapper.writeValueAsString(deploymentStatus.getConditions());
        }
        
        float fMaxSurge = 0f;
        if(maxSurge != null && !maxSurge.isEmpty())
        	fMaxSurge = Float.parseFloat(maxSurge);
        
        float fMaxUnavailable = 0f;
        if(maxUnavailable != null && !maxUnavailable.isEmpty())
        	fMaxUnavailable = Float.parseFloat(maxUnavailable);
        
        DeploymentEntity deploymentEntity = DeploymentEntity.builder()
				.deploymentName(name)
				.deploymentUid(uid)
				.image(image)
				.createdAt(LocalDateTime.now())
				.strategy(strategy)
				.selector(selector)
				.maxSurge(fMaxSurge)
				.maxUnavailable(fMaxUnavailable)
				.annotation(annotation)
				.label(label)
				.podUpdated(podUpdated)
				.podReplicas(podReplicas)
				.podReady(podReady)
				.condition(condition)
				.build();

        return deploymentEntity;
    }
}
