package kr.co.strato.domain.IngressController.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.cluster.model.ClusterEntity;

public interface IngressControllerRepository extends JpaRepository<IngressControllerEntity, Long>, CustomIngressControllerRepository  {
	
	public IngressControllerEntity findByName(String name);
	
	public IngressControllerEntity findByDefaultYn(String defaultYn);
	
	public List<IngressControllerEntity> findByCluster(ClusterEntity cluster);
	
	public List<IngressControllerEntity> findByClusterAndDefaultYn(ClusterEntity cluster, String defaultYn);
	
} 
