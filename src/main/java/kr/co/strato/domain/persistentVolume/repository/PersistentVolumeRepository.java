package kr.co.strato.domain.persistentVolume.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;

public interface PersistentVolumeRepository extends JpaRepository<PersistentVolumeEntity, Long>  {
	Page<PersistentVolumeEntity> findByName(String name, Pageable pageable);//name 조회(Page 객체 반환)	

	Optional<List<PersistentVolumeEntity>> findByStorageClassIdx(Long storageClassIdx);
	
}
