package kr.co.strato.domain.storageClass.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.storageClass.model.StorageClassEntity;

public interface CustomStorageClassRepository {
    public Page<StorageClassEntity> getStorageClassList(Pageable pageable, Long clusterId,String name);
    StorageClassEntity findByName(String name);	
}
