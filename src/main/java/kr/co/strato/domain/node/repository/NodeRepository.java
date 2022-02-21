package kr.co.strato.domain.node.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.node.model.NodeEntity;

public interface NodeRepository extends JpaRepository<NodeEntity, Long>  {
	
	Page<NodeEntity> findByName(String name, Pageable pageable);//name 조회(Page 객체 반환)
	
	Page<NodeEntity> findByClusterIdx(Long clusterIdx, Pageable pageable);//clusterIdx 조회(Page 객체 반환)
}
