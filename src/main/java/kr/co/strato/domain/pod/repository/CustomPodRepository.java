package kr.co.strato.domain.pod.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public interface CustomPodRepository {
//	public PodEntity getPodDetail(Long podId);
	
	public Page<PodEntity> getPodList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId);
	
	public List<PodEntity> findAllByNamespaceIdx(Long namespaceId);
	
	public StatefulSetEntity getPodStatefulSet(Long podId);
	
//	public void deleteByOwnerUidAndKind(String ownerUid, String kind);
}
