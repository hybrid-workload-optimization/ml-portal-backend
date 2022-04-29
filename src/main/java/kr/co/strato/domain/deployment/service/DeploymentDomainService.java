package kr.co.strato.domain.deployment.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.repository.DeploymentRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.replicaset.repository.ReplicaSetRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class DeploymentDomainService implements InNamespaceDomainService {
	
	@Autowired
	DeploymentRepository deploymentRepository;

	@Autowired
	ReplicaSetRepository replicaSetRepository;

	@Autowired
	PodRepository podRepository;
	
	public List<DeploymentEntity> getDeploymentEntities(){
		return deploymentRepository.findAll();
	}
	
	public DeploymentEntity getDeploymentEntitiy(Long idx){
		Optional<DeploymentEntity> deployment = deploymentRepository.findById(idx);
		if (deployment.isPresent())
			return deployment.get();
		else
			throw new NotFoundResourceException("deployment_idx : " + idx);
	}
	
	public DeploymentEntity save(DeploymentEntity deploymentEntity){
		DeploymentEntity result = deploymentRepository.save(deploymentEntity);
		return result;
	}
	
	public void delete(Long idx){
		DeploymentEntity deployment = DeploymentEntity.builder().deploymentIdx(idx).build();
		List<ReplicaSetEntity> replicasets =  replicaSetRepository.getByDeployment(deployment);
		replicasets.forEach((e)->{
			podRepository.deleteByOwnerUidAndKind(e.getReplicaSetUid(), ResourceType.replicaSet.get());
			replicaSetRepository.delete(e);
		});
		deploymentRepository.deleteById(idx);
	}

	public void deleteReplicaSetFromDeploymentIdx(Long idx){
		DeploymentEntity deployment = DeploymentEntity.builder().deploymentIdx(idx).build();
		List<ReplicaSetEntity> replicasets =  replicaSetRepository.getByDeployment(deployment);
		replicasets.forEach((e)->{
			podRepository.deleteByOwnerUidAndKind(e.getReplicaSetUid(), ResourceType.replicaSet.get());
			replicaSetRepository.delete(e);
		});
	}
	
	
	public void deleteByNamespaceEntity(NamespaceEntity namespace) {
		List<DeploymentEntity> deployments = deploymentRepository.findByNamespaceEntity(namespace);
		deployments.forEach((e) -> {
			delete(e.getDeploymentIdx());
		});
	}

	@Override
	public boolean isDuplicateName(Long clusterIdx, String namespace, String name) {
		DeploymentEntity entity = deploymentRepository.getDeployment(clusterIdx, namespace, name);
		return entity != null;
	}
}
