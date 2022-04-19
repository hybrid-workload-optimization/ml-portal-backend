package kr.co.strato.domain.daemonset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

public interface CustomDaemonSetRepository {

	public Page<DaemonSetEntity> getDaemonSetList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
}
