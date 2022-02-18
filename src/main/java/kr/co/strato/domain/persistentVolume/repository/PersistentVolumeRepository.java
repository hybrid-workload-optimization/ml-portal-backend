package kr.co.strato.domain.persistentVolume.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;

public interface PersistentVolumeRepository extends JpaRepository<PersistentVolumeEntity, Long>  {
	
}
