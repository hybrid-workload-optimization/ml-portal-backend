package kr.co.strato.domain.storageClass.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;

public interface StorageClassRepository extends JpaRepository<StorageClassEntity, Long>,CustomStorageClassRepository  {
	
	@Transactional
	public Integer deleteByCluster(ClusterEntity cluster);
	
}
