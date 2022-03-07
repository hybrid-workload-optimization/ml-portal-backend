package kr.co.strato.domain.ingress.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface IngressRepository extends JpaRepository<IngressEntity, Long>  {
	
	Page<IngressEntity> findByNameAndNamespace(String name,NamespaceEntity namespace, Pageable pageable);
	
}
