package kr.co.strato.domain.job.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.portal.workload.model.JobArgDto;

public interface CustomJobRepository {
	public JobEntity findByUidAndNamespaceIdx(String jobUid, NamespaceEntity namespaceEntity);
	
	Page<JobEntity> getPageList(Pageable pageable, JobArgDto args);
}
