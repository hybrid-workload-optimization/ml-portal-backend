package kr.co.strato.domain.pod.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public interface CustomPodRepository {
//	public PodEntity getPodDetail(Long podId);
	
	public Page<PodEntity> getPodList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId);
	
	public List<PodEntity> findAllByNamespaceIdx(Long namespaceId);
	
	public StatefulSetEntity getPodStatefulSet(Long podId);
//	public DaemonSetEntity getPodDaemonSet(Long podId);
	public ReplicaSetEntity getPodReplicaSet(Long podId);
	public JobEntity getPodJob(Long podId);
	
	public PodEntity getPod(Long clusterIdx, String namespace, String name);
}
