package kr.co.strato.portal.workload.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import io.fabric8.kubernetes.api.model.apps.*;
import kr.co.strato.adapter.k8s.replicaset.service.ReplicaSetAdapterService;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.service.ClusterDomainService;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.error.exception.PortalException;
import kr.co.strato.global.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.adapter.k8s.deployment.service.DeploymentAdapterService;
import kr.co.strato.portal.workload.model.DeploymentArgDto;
import kr.co.strato.portal.workload.model.DeploymentDto;
import kr.co.strato.portal.workload.model.DeploymentDtoMapper;
import lombok.extern.slf4j.Slf4j;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.repository.DeploymentRepository;
import kr.co.strato.domain.deployment.service.DeploymentDomainService;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.service.NamespaceDomainService;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.replicaset.service.ReplicaSetDomainService;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.util.Base64Util;
import org.springframework.util.CollectionUtils;

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

	@Autowired
	ClusterDomainService clusterDomainService;
	
	@Autowired
	ReplicaSetDomainService replicaSetDomainService;
	
	@Autowired
	PodRepository podRepository;

	@Autowired
	ReplicaSetAdapterService replicaSetAdapterService;

	@Autowired
	private ProjectDomainService projectDomainService;
	
	//목록
	public Page<DeploymentDto> getList(PageRequest pageRequest, DeploymentArgDto args){
		Long clusterIdx = args.getClusterIdx();
		Long namespaceIdx = args.getNamespaceIdx();
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);

		Map<String, DeploymentStatus> maps = new HashMap<>();
		try{
			List<Deployment> deployments = new ArrayList<>();
			if(namespaceIdx == null || namespaceIdx == 0){
				deployments = deploymentAdapterService.retrieveList(clusterEntity.getClusterId(), null);
			}else{
				NamespaceEntity namespaceEntity = namespaceDomainService.getDetail(namespaceIdx);
				deployments = deploymentAdapterService.retrieveList(clusterEntity.getClusterId(), namespaceEntity.getName());
			}
			maps = deployments.stream().collect(Collectors.toMap(
					e1 -> e1.getMetadata().getUid(),
					e2 -> e2.getStatus()
			));
		}catch (Exception e){
			log.error("k8s 디플로이먼트 조회 실패");
		}

		Page<DeploymentEntity> entities =  deploymentRepository.getDeploymentPageList(pageRequest.of(), args);
		Map<String, DeploymentStatus> finalMaps = maps;
		List<DeploymentDto> dtos = entities.stream().map(
				e -> {
					String uid = e.getDeploymentUid();
					if(finalMaps.containsKey(uid)){
						return DeploymentDtoMapper.INSTANCE.toDto(e, finalMaps.get(uid));
					}
					return DeploymentDtoMapper.INSTANCE.toDto(e);
				}
		).collect(Collectors.toList());

		Page<DeploymentDto> result = new PageImpl<>(dtos, pageRequest.of(), entities.getTotalElements());
		return result;
	}
	
	//상세
	public DeploymentDto get(Long idx){
		DeploymentEntity entity = deploymentDomainService.getDeploymentEntitiy(idx);
		Long clusterId = entity.getNamespaceEntity().getCluster().getClusterId();
		Long clusterIdx = entity.getNamespaceEntity().getCluster().getClusterIdx();
		String clusterName = entity.getNamespaceEntity().getCluster().getClusterName();
		ProjectEntity projectEntity = projectDomainService.getProjectDetailByClusterId(clusterIdx);
		String projectName = projectEntity.getProjectName();

		String replicaSetUid = null;
		Deployment deployment = null;
		DeploymentDto dto = null;

		List<ReplicaSetEntity> replicaSetEntities = replicaSetDomainService.getByDeplymentIdx(entity.getDeploymentIdx());
		if(replicaSetEntities != null && replicaSetEntities.size() > 0){
			replicaSetUid = replicaSetEntities.get(0).getReplicaSetUid();
		}

		try{
			deployment = deploymentAdapterService.retrieve(clusterId, entity.getNamespaceEntity().getName(), entity.getDeploymentName());
		}catch (Exception e){
			log.error("k8s 디플로이먼트 조회 실패", e);
		}

		if(deployment != null && deployment.getStatus() != null){
			DeploymentStatus status = deployment.getStatus();
			RollingUpdateDeployment rollingUpdateDeployment = deployment.getSpec().getStrategy().getRollingUpdate();

			dto = DeploymentDtoMapper.INSTANCE.toDto(entity, clusterId, replicaSetUid, status, rollingUpdateDeployment, projectName, clusterName);
		}else{
			dto = DeploymentDtoMapper.INSTANCE.toDto(entity, clusterId, replicaSetUid, projectName, clusterName);
		}

		return dto;
	}
	
	// yaml 조회
	public String getYaml(Long idx) {

		String name = null;
		String namespaceName = null;
		Long clusterId = null;

		DeploymentEntity entitiy = deploymentDomainService.getDeploymentEntitiy(idx);
		if (entitiy != null) {
			name = entitiy.getDeploymentName();
			NamespaceEntity namespaceEntity = entitiy.getNamespaceEntity();
			if (namespaceEntity != null) {
				namespaceName = namespaceEntity.getName();

				ClusterEntity cluster = namespaceEntity.getCluster();
				if (cluster != null)
					clusterId = cluster.getClusterId();
			}
		}
		String yaml = deploymentAdapterService.getYaml(clusterId, namespaceName, name);
		if (yaml != null)
			yaml = Base64Util.encode(yaml);
		return yaml;
	}
			
	
	//생성
	public void create(DeploymentArgDto deploymentArgDto){
		Long clusterIdx = deploymentArgDto.getClusterIdx();
		ClusterEntity clusterEntity = clusterDomainService.get(clusterIdx);
		deploymentArgDto.setClusterId(clusterIdx);
		
		save(deploymentArgDto, clusterEntity);
	}
	
	//수정
	public void update(DeploymentArgDto deploymentArgDto){
		Long deploymentIdx = deploymentArgDto.getDeploymentIdx();
		DeploymentEntity deploymentEntity  = deploymentDomainService.getDeploymentEntitiy(deploymentIdx);
		NamespaceEntity namespaceEntity = deploymentEntity.getNamespaceEntity();
		ClusterEntity clusterEntity = namespaceEntity.getCluster();

		save(deploymentArgDto, clusterEntity);
	}
	
	private void save(DeploymentArgDto deploymentArgDto, ClusterEntity clusterEntity){
		String yaml = deploymentArgDto.getYaml();
		
		List<Deployment> deployments = deploymentAdapterService.create(clusterEntity.getClusterId(), yaml);

		//deployment 저장.
		List<DeploymentEntity> eneities = deployments.stream().map(d -> {
			DeploymentEntity deploymentEntity = null;
			NamespaceEntity namespaceEntity = null;

			try {
				Deployment deployment = deploymentAdapterService.retrieve(clusterEntity.getClusterId(), d.getMetadata().getNamespace(), d.getMetadata().getName());
				deploymentEntity = toEntity(deployment);
			} catch (JsonProcessingException e) {
				log.debug(yaml);
			}
			
			if(deploymentEntity != null) {
				List<NamespaceEntity> namespaceEntities = namespaceDomainService.findByNameAndClusterIdx(d.getMetadata().getNamespace(), clusterEntity);
				if(namespaceEntities != null && namespaceEntities.size() > 0){
					namespaceEntity = namespaceEntities.get(0);
					deploymentEntity.setNamespaceEntity(namespaceEntity);
				}
				
				//수정시
				if(deploymentArgDto.getDeploymentIdx() != null)
					deploymentEntity.setDeploymentIdx(deploymentArgDto.getDeploymentIdx());
				
				deploymentDomainService.save(deploymentEntity);
			}

			//레플리카셋 저장
			try{
				List<ReplicaSet> replicaSets = replicaSetAdapterService.getListFromOwnerUid(clusterEntity.getClusterId(), deploymentEntity.getDeploymentUid());
				NamespaceEntity finalNamespaceEntity = namespaceEntity;
				DeploymentEntity finalDeploymentEntity = deploymentEntity;

				replicaSets.stream().forEach(r -> {
					ReplicaSetEntity replicaSetEntity = null;
					try {
						replicaSetEntity = toReplicaSetEntity(r, finalNamespaceEntity, finalDeploymentEntity);

						//수정 시에는 기존 레플리카셋 삭제
						if(deploymentArgDto.getDeploymentIdx() != null){
							deploymentDomainService.deleteReplicaSetFromDeploymentIdx(deploymentArgDto.getDeploymentIdx());
						}
						replicaSetDomainService.register(replicaSetEntity);
					} catch (Exception e) {
						log.error("레플리카셋 저장 실패", e);
					}
				});
			}catch (Exception e) {
				log.error("레플리카셋 저장 실패", e);
			}
			return deploymentEntity;
		}).collect(Collectors.toList());
	}
	
	//삭제
	public void delete(DeploymentArgDto deploymentArgDto){
		Long deploymentIdx = deploymentArgDto.getDeploymentIdx();
		DeploymentEntity deploymentEntity  = deploymentDomainService.getDeploymentEntitiy(deploymentIdx);
		NamespaceEntity namespaceEntity = deploymentEntity.getNamespaceEntity();
		ClusterEntity clusterEntity = namespaceEntity.getCluster();
		String deploymentName = null;
		String namespaceName = null;
		Long clusterId = null;

		if(clusterEntity != null)
			clusterId = clusterEntity.getClusterId();

		if(namespaceEntity != null)
			namespaceName = namespaceEntity.getName();

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

	private ReplicaSetEntity toReplicaSetEntity(ReplicaSet r, NamespaceEntity namespace, DeploymentEntity deployment) throws Exception {
		ObjectMapper mapper = new ObjectMapper();

		String name			= r.getMetadata().getName();
		String uid			= r.getMetadata().getUid();
		String image		= r.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
		String selector		= mapper.writeValueAsString(r.getSpec().getSelector().getMatchLabels());
		String label		= mapper.writeValueAsString(r.getMetadata().getLabels());
		String annotations	= mapper.writeValueAsString(r.getMetadata().getAnnotations());
		String createAt		= r.getMetadata().getCreationTimestamp();

		ReplicaSetEntity result = ReplicaSetEntity.builder()
				.replicaSetName(name)
				.replicaSetUid(uid)
				.image(image)
				.selector(selector)
				.label(label)
				.annotation(annotations)
				.createdAt(DateUtil.convertDateTime(createAt))
				.namespace(namespace)
				.deployment(deployment)
				.build();

		return result;
	}
}
