package kr.co.strato.domain.replicaset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

public interface CustomReplicaSetRepository {

	public Page<ReplicaSetEntity> getReplicaSetList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
	
}
