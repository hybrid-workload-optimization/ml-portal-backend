package kr.co.strato.domain.persistentVolumeClaim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.repository.CustomPodRepository;

public interface PersistentVolumeClaimRepository extends JpaRepository<PersistentVolumeClaimEntity, Long>, CustomPersistentVolumeClaimRepository  {
	public PersistentVolumeClaimEntity findByNameAndNamespaceId(String name, Long namespaceIdx);
}
