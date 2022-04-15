package kr.co.strato.domain.node.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.repository.NodeRepository;
import kr.co.strato.domain.pod.service.PodDomainService;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class NodeDomainService {

	@Autowired
	private NodeRepository nodeRepository;
	
	@Autowired
	private PodDomainService podDomainService;
	
	public Long register(NodeEntity nodeEntity) {
		
		nodeRepository.save(nodeEntity);
		return nodeEntity.getId();
	}
	public Page<NodeEntity> getList(Pageable pageable) {
		return nodeRepository.findAll(pageable);
	}

	public Page<NodeEntity> getNodeList(Pageable pageable, Long clusterId,String name) {
		return nodeRepository.getNodeList(pageable,  clusterId,name);
	}
	
	public List<NodeEntity> getNodeList(Long clusterId) {
		return nodeRepository.getNodeList(clusterId);
	}
	
	
	public boolean delete(Long id) {
		Optional<NodeEntity> opt = nodeRepository.findById(id);
		if (opt.isPresent()) {
			NodeEntity entity = opt.get();
			nodeRepository.delete(entity);
		}
		return true;
	}
	
	public NodeEntity getDetail(Long id) {
		Optional<NodeEntity> node = nodeRepository.findById(id);
		if (node.isPresent()) {
			return node.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}
	public Page<NodeEntity> findByClusterIdx(ClusterEntity clusterEntity,Pageable pageable) {
		return nodeRepository.findByClusterIdx(clusterEntity,pageable);
	}
	
	public NodeEntity findNodeName(Long clusterIdx, String name) {
		return nodeRepository.findNodeName(clusterIdx, name);
	}
	
	public void deleteByClusterIdx(Long clusterIdx) {
		List<NodeEntity> list = getNodeList(clusterIdx);
		list.forEach((node) -> {
			podDomainService.deleteByNode(node);
			delete(node.getId());
		});
	}
	
}
