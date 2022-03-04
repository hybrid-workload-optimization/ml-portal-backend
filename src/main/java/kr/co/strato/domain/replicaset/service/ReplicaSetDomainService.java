package kr.co.strato.domain.replicaset.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
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

	public ReplicaSetEntity get(Long replicaSetIdx) {
		Optional<ReplicaSetEntity> replicaSet = replicaSetRepository.findById(replicaSetIdx);
		if (replicaSet.isPresent()) {
			return replicaSet.get();
		} else {
			throw new NotFoundResourceException("cluster_idx : " + replicaSetIdx.toString());
		}
	}
	
}
