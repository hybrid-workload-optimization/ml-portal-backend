package kr.co.strato.domain.statefulset.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public interface CustomStatefulSetRepository {
    public Page<StatefulSetEntity> getStatefulSetList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId);
    
    public StatefulSetEntity findByUidAndNamespaceIdx(String statefulSetUid, NamespaceEntity namespaceEntity);
    
    public StatefulSetEntity getStatefulSet(Long clusterIdx, String namespace, String name);
}
