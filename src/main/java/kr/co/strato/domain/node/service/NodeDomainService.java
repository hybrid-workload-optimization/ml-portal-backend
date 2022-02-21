package kr.co.strato.domain.node.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.repository.NodeRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class NodeDomainService {

	@Autowired
	private NodeRepository nodeRepository;
	
	public Long register(NodeEntity nodeEntity) {
		
		nodeRepository.save(nodeEntity);
		return nodeEntity.getId();
	}
	public Page<NodeEntity> getList(Pageable pageable) {
		return nodeRepository.findAll(pageable);
	}
	
	public void delete(NodeEntity nodeEntity) {
		nodeRepository.delete(nodeEntity);
	}
	
	public NodeEntity getDetail(Long id) {
		Optional<NodeEntity> node = nodeRepository.findById(id);
		if (node.isPresent()) {
			return node.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}
}
