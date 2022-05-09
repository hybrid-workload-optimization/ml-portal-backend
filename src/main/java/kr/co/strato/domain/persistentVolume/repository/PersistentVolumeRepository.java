package kr.co.strato.domain.persistentVolume.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;

public interface PersistentVolumeRepository extends JpaRepository<PersistentVolumeEntity, Long>, CustomPersistentVolumeRepository{
	
	@Transactional
	public Integer deleteByCluster(ClusterEntity cluster);
	
	public PersistentVolumeEntity findByNameAndCluster(String name, ClusterEntity cluster);
	
}
