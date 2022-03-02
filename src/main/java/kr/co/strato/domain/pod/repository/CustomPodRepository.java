package kr.co.strato.domain.pod.repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.pod.model.PodEntity;

public interface CustomPodRepository {
	public Page<PodEntity> getPodList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId);
	
}
