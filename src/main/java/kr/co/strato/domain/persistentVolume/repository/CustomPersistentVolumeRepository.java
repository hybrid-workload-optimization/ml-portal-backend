package kr.co.strato.domain.persistentVolume.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;

public interface CustomPersistentVolumeRepository {
    public Page<PersistentVolumeEntity> getPersistentVolumeList(Pageable pageable, Long clusterId,String name);
	
    List<PersistentVolumeEntity> findByStorageClassIdx(Long storageClassIdx);
}
