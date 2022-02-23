package kr.co.strato.domain.storageClass.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.repository.PersistentVolumeRepository;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;
import kr.co.strato.domain.storageClass.repository.StorageClassRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class StorageClassDomainService {

	@Autowired
	private StorageClassRepository storageClassRepository;
	
	@Autowired
	private PersistentVolumeRepository persistentVolumeRepository;
	
	
	public Page<StorageClassEntity> findByName(String name,Pageable pageable) {
		return storageClassRepository.findByName(name,pageable);
	}
	
	public StorageClassEntity getDetail(Long id) {
		Optional<StorageClassEntity> storageClass = storageClassRepository.findById(id);
		if (storageClass.isPresent()) {
			return storageClass.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}

	public Long register(StorageClassEntity storageClassEntity) {
		storageClassRepository.save(storageClassEntity);
		return storageClassEntity.getId();
	}
	
	public Long update(StorageClassEntity storageClassEntity,Long storageClassId, Long clusterId) {
		storageClassRepository.save(storageClassEntity);
		return storageClassEntity.getId();
	}
	
	public void delete(StorageClassEntity storageClassEntity) {
		storageClassRepository.delete(storageClassEntity);
	}
	
	public Optional<List<PersistentVolumeEntity>> persistentVolumeStorageClassIdx(Long StorageClassIdx) {
		return persistentVolumeRepository.findByStorageClassIdx(StorageClassIdx);
	}
	
	public ClusterEntity getCluster(Long id) {
		StorageClassEntity entity = getDetail(id);
		ClusterEntity cluster = entity.getClusterIdx();
		return cluster;
	}

	
}
