package kr.co.strato.domain.namespace.service;


import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
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
		Optional<NamespaceEntity> namespace = namespaceRepository.findById(id);
		if (namespace.isPresent()) {
			return namespace.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}
	
	public List<NamespaceEntity> findByNameAndClusterIdx(String name,ClusterEntity clusterEntity) {
		return namespaceRepository.findByNameAndClusterIdx(name,clusterEntity);
	}
	
	public void update(NamespaceEntity namespaceEntity) {
		Optional<NamespaceEntity> namespace = namespaceRepository.findById(namespaceEntity.getId());
		if (namespace.isPresent()) {
			namespaceRepository.save(namespaceEntity);
		} else {
			throw new NotFoundResourceException("namespace_id : " + namespaceEntity.getId());
		}
	}
	
}
