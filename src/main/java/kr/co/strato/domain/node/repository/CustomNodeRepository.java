package kr.co.strato.domain.node.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;

public interface CustomNodeRepository {
    public Page<NodeEntity> getNodeList(Pageable pageable, Long clusterId,String name);
    
    public Page<NodeEntity> findByClusterIdx(ClusterEntity clusterEntity, Pageable pageable);//clusterIdx 조회(Page 객체 반환)

}
