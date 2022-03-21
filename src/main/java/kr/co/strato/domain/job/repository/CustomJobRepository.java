package kr.co.strato.domain.job.repository;

import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface CustomJobRepository {
	public JobEntity findByUidAndNamespaceIdx(String jobUid, NamespaceEntity namespaceEntity);
}
