package kr.co.strato.domain.namespace.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface NamespaceRepository extends JpaRepository<NamespaceEntity, Long> , CustomNamespaceRepository {
	
}
