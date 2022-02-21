package kr.co.strato.domain.namespace.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface NamespaceRepository extends JpaRepository<NamespaceEntity, Long>  {
	Page<NamespaceEntity> findByName(String name, Pageable pageable);//name 조회(Page 객체 반환)	
	
	List<NamespaceEntity> findByNameAndClusterIdx(String name, ClusterEntity clusterEntity);//name clusterEntity 조회
}
