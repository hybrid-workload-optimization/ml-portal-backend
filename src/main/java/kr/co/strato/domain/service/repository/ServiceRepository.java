package kr.co.strato.domain.service.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.service.model.ServiceEntity;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long>, CustomServiceRepository {

	public List<ServiceEntity> findByNamespace(NamespaceEntity namespace);
}
