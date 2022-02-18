package kr.co.strato.domain.storageClass.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.storageClass.model.StorageClassEntity;

public interface StorageClassRepository extends JpaRepository<StorageClassEntity, Long>  {
	
}
