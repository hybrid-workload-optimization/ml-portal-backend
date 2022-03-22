package kr.co.strato.domain.persistentVolume.service;


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
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
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
	
	
	public Long update(PersistentVolumeEntity updateEntity,Long persistentVolumeId) {
		PersistentVolumeEntity oldEntity = get(persistentVolumeId);
	    changeToNewData(oldEntity, updateEntity);
	    persistentVolumeRepository.save(oldEntity);
		return oldEntity.getId();
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
    
    public List<PersistentVolumeEntity> findByStorageClassIdx(Long storageClassIdx){
		return persistentVolumeRepository.findByStorageClassIdx(storageClassIdx);
	}
    
    public PersistentVolumeEntity get(Long persistentVolumeId){
    	PersistentVolumeEntity persistentVolumeEntity = persistentVolumeRepository.findById(persistentVolumeId)
                .orElseThrow(() -> new NotFoundResourceException("persistentVolumeId id:"+persistentVolumeId));
        return persistentVolumeEntity;
    }

    
    private void changeToNewData(PersistentVolumeEntity oldEntity, PersistentVolumeEntity newEntity){
        oldEntity.setUid(newEntity.getUid());
        oldEntity.setStatus(newEntity.getStatus());
        oldEntity.setAccessMode(newEntity.getAccessMode());
        oldEntity.setClaim(newEntity.getClaim());
        oldEntity.setReclaim(newEntity.getReclaim());
        oldEntity.setReclaimPolicy(newEntity.getReclaimPolicy());
        oldEntity.setResourceName(newEntity.getResourceName());
        oldEntity.setSize(newEntity.getSize());
        oldEntity.setType(newEntity.getType());
        oldEntity.setPath(newEntity.getPath());
        oldEntity.setCreatedAt(newEntity.getCreatedAt());
        oldEntity.setAnnotation(newEntity.getAnnotation());
        oldEntity.setLabel(newEntity.getLabel());
    }

	
}
