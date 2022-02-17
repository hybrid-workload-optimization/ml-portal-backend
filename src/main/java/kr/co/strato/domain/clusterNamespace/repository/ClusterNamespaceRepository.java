package kr.co.strato.domain.clusterNamespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.clusterNamespace.model.ClusterNamespace;

public interface ClusterNamespaceRepository extends JpaRepository<ClusterNamespace, Long>  {
	
}
