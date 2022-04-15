package kr.co.strato.domain.ingress.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface IngressRepository extends JpaRepository<IngressEntity, Long> ,CustomIngressRepository {
	
	@Transactional
	public Integer deleteByNamespace(NamespaceEntity namespace);
}
