package kr.co.strato.domain.storageClass.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
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
	
	
	public Page<StorageClassEntity> getStorageClassList(Pageable pageable, Long clusterId,String name) {
		return storageClassRepository.getStorageClassList(pageable,clusterId,name);
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
	
	public boolean delete(Long id) {
		Optional<StorageClassEntity> opt = storageClassRepository.findById(id);
		if (opt.isPresent()) {
			StorageClassEntity entity = opt.get();
			storageClassRepository.delete(entity);
		}
		return true;
	}
	
	public void delete(StorageClassEntity storageClassEntity) {
		storageClassRepository.delete(storageClassEntity);
	}
	
	public List<PersistentVolumeEntity> persistentVolumeStorageClassIdx(Long StorageClassIdx) {
		return persistentVolumeRepository.findByStorageClassIdx(StorageClassIdx);
	}
	
	public ClusterEntity getCluster(Long id) {
		StorageClassEntity entity = getDetail(id);
		ClusterEntity cluster = entity.getCluster();
		return cluster;
	}

	
}
