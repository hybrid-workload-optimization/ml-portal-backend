package kr.co.strato.domain.configMap.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.repository.CustomPersistentVolumeClaimRepository;

public interface ConfigMapRepository extends JpaRepository<ConfigMapEntity, Long>, CustomConfigMapRepository {

	public Page<ConfigMapEntity> getConfigMapList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
}
