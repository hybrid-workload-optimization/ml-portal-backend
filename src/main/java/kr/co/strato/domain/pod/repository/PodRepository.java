package kr.co.strato.domain.pod.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.pod.model.PodEntity;

public interface PodRepository extends JpaRepository<PodEntity, Long>, CustomPodRepository {
	
	@Transactional
	public void deleteByOwnerUidAndKind(String ownerUid, String kind);
	
	public List<PodEntity> findByNode(NodeEntity nodeEntity);
	
	@Transactional
	public void deleteByCluster(ClusterEntity cluster);
	
}
