package kr.co.strato.domain.configMap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;

public interface CustomConfigMapRepository {

	public Page<ConfigMapEntity> getConfigMapList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
}
