package kr.co.strato.domain.node.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.node.model.NodeEntity;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> , CustomNodeRepository {
	
}
