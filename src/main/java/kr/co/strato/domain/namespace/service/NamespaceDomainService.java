package kr.co.strato.domain.namespace.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class NamespaceDomainService {
	
	@Autowired
	private NamespaceRepository namespaceRepository;
	
	public Long register(NamespaceEntity namespaceEntity) {
		namespaceRepository.save(namespaceEntity);
		return namespaceEntity.getId();
	}
	public Page<NamespaceEntity> getList(Pageable pageable) {
		return namespaceRepository.findAll(pageable);
	}
	
	public Page<NamespaceEntity> findByName(String name,Pageable pageable) {
		return namespaceRepository.findByName(name,pageable);
	}
	
	public void delete(NamespaceEntity namespaceEntity) {
		namespaceRepository.delete(namespaceEntity);
	}
	
	public NamespaceEntity getDetail(Long id) {
		Optional<NamespaceEntity> node = namespaceRepository.findById(id);
		if (node.isPresent()) {
			return node.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}

}
