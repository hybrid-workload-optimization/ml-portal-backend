package kr.co.strato.domain.daemonset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.daemonset.model.DaemonSetEntity;

public interface CustomDaemonSetRepository {

	public Page<DaemonSetEntity> getDaemonSetList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
	public DaemonSetEntity getDaemonSet(Long clusterIdx, String namespace, String name);
}
