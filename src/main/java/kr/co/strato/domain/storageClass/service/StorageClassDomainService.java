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
	
	public Long update(StorageClassEntity updateEntity,Long storageClassId) {
		StorageClassEntity oldEntity = get(storageClassId);
        changeToNewData(oldEntity, updateEntity);
		storageClassRepository.save(oldEntity);
		return oldEntity.getId();
	}
	
	public boolean delete(Long id) {
		storageClassRepository.deleteStorageClass(id);
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
	
	public StorageClassEntity get(Long storageClassId) {
		StorageClassEntity namespaceEntity = storageClassRepository.findById(storageClassId)
				.orElseThrow(() -> new NotFoundResourceException("storageClass id:" + storageClassId));

		return namespaceEntity;
	}

	private void changeToNewData(StorageClassEntity oldEntity, StorageClassEntity newEntity) {
		oldEntity.setUid(newEntity.getUid());
		oldEntity.setCreatedAt(newEntity.getCreatedAt());
		oldEntity.setProvider(newEntity.getProvider());
		oldEntity.setType(newEntity.getType());
		oldEntity.setAnnotation(newEntity.getAnnotation());
		oldEntity.setLabel(newEntity.getLabel());
	}

	
}
