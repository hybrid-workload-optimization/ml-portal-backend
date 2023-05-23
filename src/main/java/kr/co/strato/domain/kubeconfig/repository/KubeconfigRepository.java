package kr.co.strato.domain.kubeconfig.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.kubeconfig.model.KubeconfigEntity;

public interface KubeconfigRepository extends JpaRepository<KubeconfigEntity, Long> {
	
}
