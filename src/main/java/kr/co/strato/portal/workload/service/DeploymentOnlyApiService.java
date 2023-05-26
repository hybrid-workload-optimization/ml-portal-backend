package kr.co.strato.portal.workload.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.adapter.k8s.deployment.service.DeploymentAdapterService;
import kr.co.strato.adapter.k8s.replicaset.service.ReplicaSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.Base64Util;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.workload.model.DeploymentArgDto;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.DeploymentArgDto.UpdateParam;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class DeploymentOnlyApiService {
	
	@Autowired
	DeploymentAdapterService deploymentAdapterService;

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ReplicaSetAdapterService replicaSetAdapterService;
	
	
	//목록
	public Page<DeploymentDto> getList(PageRequest pageRequest, DeploymentArgDto.ListParam args) {
		Long clusterIdx = args.getClusterIdx();
		String namespace = args.getNamespace();
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		String clusterName = clusterEntity.getClusterName();
		Long kubeConfigId = clusterEntity.getClusterId();
		
		List<DeploymentDto> list = new ArrayList<>();
		
		List<Deployment> deployments = deploymentAdapterService.retrieveList(kubeConfigId, namespace);
		for(Deployment d : deployments) {
			DeploymentDto dto;
			try {
				dto = toDto(d);
				dto.setClusterName(clusterName);
				dto.setClusterIdx(clusterIdx);
				dto.setClusterId(Long.toString(kubeConfigId));
				list.add(dto);
			} catch (JsonProcessingException e) {
				log.error("", e);
			}
			
		}
		Page<DeploymentDto> result = new PageImpl<>(list, pageRequest.of(), list.size());
		return result;
	}
	
	//상세
	public DeploymentDto get(Long clusterIdx, String namespace, String name) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
		
		Deployment deployment = deploymentAdapterService.retrieve(kubeConfigId, namespace, name);
		DeploymentDto dto = null;;
		try {
			dto = toDto(deployment);
			dto.setClusterName(clusterEntity.getClusterName());	
			dto.setClusterIdx(clusterIdx);
			dto.setClusterId(Long.toString(kubeConfigId));
			
			try {
			
				List<ReplicaSet> replicaSets = replicaSetAdapterService.getListFromOwnerUid(clusterEntity.getClusterId(), dto.getUid());
				if(replicaSets != null && replicaSets.size() > 0) {
					ReplicaSet replicaSet = replicaSets.get(0);
					String uid = replicaSet.getMetadata().getUid();
					dto.setReplicaSetUid(uid);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			
		} catch (JsonProcessingException e) {
			log.error("", e);
		}
		
		return dto;
	}
	
	// yaml 조회
	public String getYaml(Long clusterIdx, String namespace, String name) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
		
		String yaml = deploymentAdapterService.getYaml(kubeConfigId, namespace, name);
		yaml = Base64Util.encode(yaml);
		return yaml;
	}
			
	
	
	//수정
	public void update(UpdateParam deploymentArgDto) {
		ClusterEntity clusterEntity = clusterDomainService.get(deploymentArgDto.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		String yaml = deploymentArgDto.getYaml();
		
		deploymentAdapterService.create(kubeConfigId, yaml);
	}
	
	public void save(DeploymentArgDto deploymentArgDto) {
		ClusterEntity clusterEntity = clusterDomainService.get(deploymentArgDto.getClusterIdx());
		Long kubeConfigId = clusterEntity.getClusterId();
		String yaml = deploymentArgDto.getYaml();
		
		deploymentAdapterService.create(kubeConfigId, yaml);
	}
	
	//삭제
	public void delete(Long clusterIdx, String namespace, String name) {
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		Long kubeConfigId = clusterEntity.getClusterId();
		
		deploymentAdapterService.delete(kubeConfigId, namespace, name);
	}
	
    private DeploymentDto toDto(Deployment d) throws JsonProcessingException {
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
        String image = null;
        
        String createAt = null;
        String namespace = null;
        
        io.fabric8.kubernetes.api.model.ObjectMeta metadata = d.getMetadata();
        if(metadata != null) {        
        	name = metadata.getName();
        	uid = metadata.getUid();
        	annotation = mapper.writeValueAsString(metadata.getAnnotations());
        	label = mapper.writeValueAsString(metadata.getLabels());
        	createAt = metadata.getCreationTimestamp();
        	namespace = metadata.getNamespace();
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
        	if(deploymentSpec.getSelector() != null){
				//
				selector = mapper.writeValueAsString(deploymentSpec.getSelector().getMatchLabels());
			}

			image = deploymentSpec.getTemplate().getSpec().getContainers().get(0).getImage();
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
        
       
        
        DeploymentDto deploymentDto = DeploymentDto.builder()
        		.name(name)
				.uid(uid)
				.image(image)
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
				.createdAt(DateUtil.strToLocalDateTime(createAt))
        		.namespaceName(namespace)
				.build();

        return deploymentDto;
    }
}
