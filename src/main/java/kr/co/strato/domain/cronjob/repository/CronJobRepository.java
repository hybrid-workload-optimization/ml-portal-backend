package kr.co.strato.domain.cronjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface CronJobRepository extends JpaRepository<CronJobEntity, Long>, CustomCronJobRepository {
	
	@Transactional
	public Integer deleteByNamespaceEntity(NamespaceEntity namespaceEntity);
}
