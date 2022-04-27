package kr.co.strato.domain.secret.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.secret.model.SecretEntity;

public interface CustomSecretRepository {

	public Page<SecretEntity> getSecretList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
}
