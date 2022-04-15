package kr.co.strato.domain.replicaset.service;

import java.util.List;
import java.util.Optional;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.replicaset.repository.ReplicaSetRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class ReplicaSetDomainService {

	@Autowired
	ReplicaSetRepository replicaSetRepository;
	
	
	public Long register(ReplicaSetEntity replicaSetEntity) {
		replicaSetRepository.save(replicaSetEntity);
		
		return replicaSetEntity.getReplicaSetIdx();
	}
	
	public void delete(ReplicaSetEntity replicaSetEntity) {
		replicaSetRepository.delete(replicaSetEntity);
	}

	public Page<ReplicaSetEntity> getList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
        return replicaSetRepository.getReplicaSetList(pageable, projectIdx, clusterIdx, namespaceIdx);
    }
	
	public ReplicaSetEntity get(Long replicaSetIdx) {
		Optional<ReplicaSetEntity> replicaSet = replicaSetRepository.findById(replicaSetIdx);
		if (replicaSet.isPresent()) {
			return replicaSet.get();
		} else {
			throw new NotFoundResourceException("cluster_idx : " + replicaSetIdx.toString());
		}
	}
	
	public List<ReplicaSetEntity> getByDeplymentIdx(Long deploymentIdx){
		DeploymentEntity deployment = DeploymentEntity.builder().deploymentIdx(deploymentIdx).build();
		return replicaSetRepository.getByDeployment(deployment);
	}
	
	public Integer deleteByNamespaceEntity(NamespaceEntity namespace) {
		return replicaSetRepository.deleteByNamespace(namespace);
	}
}
