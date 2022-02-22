package kr.co.strato.domain.statefulset.repository;

import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CustomStatefulSetRepository {
    public Page<StatefulSetEntity> getStatefulSetList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId);
}
