package kr.co.strato.domain.replicaset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public interface CustomReplicaSetRepository {

	public Page<ReplicaSetEntity> getReplicaSetList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
	
	public ReplicaSetEntity findByUidAndNamespaceIdx(String replicaSetUid, NamespaceEntity namespaceEntity);
	
}
