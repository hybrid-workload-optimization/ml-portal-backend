package kr.co.strato.domain.persistentVolumeClaim.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;

public interface PersistentVolumeClaimRepository extends JpaRepository<PersistentVolumeClaimEntity, Long>  {
	public PersistentVolumeClaimEntity findByNameAndNamespaceId(String name, Long namespaceIdx);
}
