package kr.co.strato.domain.clusterNamespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.clusterNamespace.model.ClusterNamespaceEntity;

public interface ClusterNamespaceRepository extends JpaRepository<ClusterNamespaceEntity, Long>  {
	
}
