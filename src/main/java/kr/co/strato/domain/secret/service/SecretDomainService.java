package kr.co.strato.domain.secret.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.secret.model.SecretEntity;
import kr.co.strato.domain.secret.repository.SecretRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class SecretDomainService implements InNamespaceDomainService {

	@Autowired
	private SecretRepository secretRepository;
	
	public Long register(SecretEntity secretEntity) {
		secretRepository.save(secretEntity);
		
		return secretEntity.getId();
	}
	
	public Page<SecretEntity> getList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		return secretRepository.getSecretList(pageable, projectIdx, clusterIdx, namespaceIdx);
    }
	
	public SecretEntity get(Long secretIdx) {
		Optional<SecretEntity> secret = secretRepository.findById(secretIdx);
		if (secret.isPresent()) {
			return secret.get();
		} else {
			throw new NotFoundResourceException("Secret : " + secret.toString());
		}
	}
	
	public void delete(SecretEntity secretEntity) {
		secretRepository.delete(secretEntity);
	}

	@Override
	public boolean isDuplicateName(Long clusterIdx, String namespace, String name) {
		Object obj = secretRepository.getSecret(clusterIdx, namespace, name);
		return obj != null;
	}
}
