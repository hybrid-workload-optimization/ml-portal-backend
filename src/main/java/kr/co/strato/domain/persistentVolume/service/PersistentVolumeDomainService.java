package kr.co.strato.domain.persistentVolume.service;


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
public class PersistentVolumeDomainService {

	@Autowired
	private PersistentVolumeRepository persistentVolumeRepository;
	
	@Autowired
	private StorageClassRepository storageClassRepository;
	
	public Page<PersistentVolumeEntity> getPersistentVolumeList(Pageable pageable, Long clusterId,String name){
		return persistentVolumeRepository.getPersistentVolumeList(pageable,clusterId,name);
	}
	
	public PersistentVolumeEntity getDetail(Long id) {
		PersistentVolumeEntity persistentVolume = persistentVolumeRepository.findPersistentVolumeDetail(id);
		return persistentVolume;
	}

	public Long register(PersistentVolumeEntity persistentVolumeEntity) {
		persistentVolumeRepository.save(persistentVolumeEntity);
		return persistentVolumeEntity.getId();
	}
	
	
	public Long update(PersistentVolumeEntity persistentVolumeEntity,Long persistentVolumeId, Long clusterId) {
		persistentVolumeRepository.save(persistentVolumeEntity);
		return persistentVolumeEntity.getId();
	}

	public void delete(PersistentVolumeEntity persistentVolumeEntity) {
		persistentVolumeRepository.delete(persistentVolumeEntity);
	}
	
	public boolean delete(Long id) {
		Optional<PersistentVolumeEntity> opt = persistentVolumeRepository.findById(id);
		if (opt.isPresent()) {
			PersistentVolumeEntity entity = opt.get();
			persistentVolumeRepository.delete(entity);
		}
		return true;
	}
	
    public StorageClassEntity getStorageClassId(String name){
        StorageClassEntity storageClass = storageClassRepository.findByName(name);
		return storageClass;
    }
	
    public ClusterEntity getCluster(Long id){
    	PersistentVolumeEntity entity = getDetail(id);
        ClusterEntity cluster =  entity.getCluster();

        return cluster;
    }

	
}
