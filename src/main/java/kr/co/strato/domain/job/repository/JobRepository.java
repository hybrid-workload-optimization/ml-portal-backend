package kr.co.strato.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface JobRepository extends JpaRepository<JobEntity, Long>, CustomJobRepository {
	
	@Transactional
	public Integer deleteByNamespaceEntity(NamespaceEntity namespace);
}
