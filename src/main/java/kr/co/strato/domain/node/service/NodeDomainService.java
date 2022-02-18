package kr.co.strato.domain.node.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.repository.NodeRepository;

@Service
public class NodeDomainService {

	@Autowired
	private NodeRepository nodeRepository;
	
	public Long register(NodeEntity node) {
		
		nodeRepository.save(node);
		return node.getId();
	}
	public Page<NodeEntity> getList(Pageable pageable) {
		return nodeRepository.findAll(pageable);
	}
}
