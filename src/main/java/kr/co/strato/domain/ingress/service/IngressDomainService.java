package kr.co.strato.domain.ingress.service;


import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.repository.IngressRepository;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class IngressDomainService {
	
	@Autowired
	private IngressRepository ingressRepository;
	
	public Long register(IngressEntity ingressEntity) {
		ingressRepository.save(ingressEntity);
		return ingressEntity.getId();
	}
	public Page<IngressEntity> getList(Pageable pageable) {
		return ingressRepository.findAll(pageable);
	}
	
	public Page<IngressEntity> findByName(String name,NamespaceEntity namespace,Pageable pageable) {
		return ingressRepository.findByNameAndNamespace(name, namespace,pageable);
	}
	
	public boolean delete(Long id) {
		Optional<IngressEntity> opt = ingressRepository.findById(id);
		if (opt.isPresent()) {
			IngressEntity entity = opt.get();
			ingressRepository.delete(entity);
		}
		return true;
	}
	
	public IngressEntity getDetail(Long id) {
		Optional<IngressEntity> ingress = ingressRepository.findById(id);
		if (ingress.isPresent()) {
			return ingress.get();
		} else {
			throw new NotFoundResourceException(id.toString());
		}
	}
	
    public Long update(IngressEntity ingressEntity,Long namespaceId, Long clusterId) {
    	ingressRepository.save(ingressEntity);
		return ingressEntity.getId();
	}
	
}
