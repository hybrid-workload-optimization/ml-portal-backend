package kr.co.strato.domain.secret.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.configMap.repository.CustomConfigMapRepository;
import kr.co.strato.domain.secret.model.SecretEntity;

public interface SecretRepository extends JpaRepository<SecretEntity, Long>, CustomSecretRepository {

	public Page<SecretEntity> getSecretList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
}
