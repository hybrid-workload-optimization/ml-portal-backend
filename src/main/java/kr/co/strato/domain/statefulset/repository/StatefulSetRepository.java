package kr.co.strato.domain.statefulset.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public interface StatefulSetRepository extends JpaRepository<StatefulSetEntity, Long>, CustomStatefulSetRepository {
	
	public List<StatefulSetEntity> findByNamespace(NamespaceEntity namespace);
}
