package kr.co.strato.domain.persistentVolume.service;


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
public class PersistentVolumeDomainService {

	@Autowired
	private PersistentVolumeRepository persistentVolumeRepository;
	
	@Autowired
	private StorageClassRepository storageClassRepository;
	
	public Page<PersistentVolumeEntity> findByName(String name,Pageable pageable) {
		return persistentVolumeRepository.findByName(name,pageable);
	}
	
	public PersistentVolumeEntity getDetail(Long id) {
		Optional<PersistentVolumeEntity> persistentVolume = persistentVolumeRepository.findById(id);
		if (persistentVolume.isPresent()) {
			return persistentVolume.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
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
	
    public StorageClassEntity getStorageClassId(String name){
        Optional<StorageClassEntity> StorageClass = storageClassRepository.findByName(name);
        if(StorageClass.isPresent()){
            return StorageClass.get();
        }else{
            throw new NotFoundResourceException(name.toString());
        }
    }
	
    public ClusterEntity getCluster(Long id){
    	PersistentVolumeEntity entity = getDetail(id);
        ClusterEntity cluster =  entity.getClusterIdx();

        return cluster;
    }

	
}
