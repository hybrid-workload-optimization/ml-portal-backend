package kr.co.strato.domain.namespace.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface CustomNamespaceRepository {
    public Page<NamespaceEntity> getNamespaceList(Pageable pageable, Long clusterId,String name);
    List<NamespaceEntity> findByNameAndClusterIdx(String name, ClusterEntity clusterEntity);//name clusterEntity 조회
	List<NamespaceEntity> findByClusterIdx(ClusterEntity clusterIdx);
	
	public void deleteNamespace(Long namespaceId);
}
