package kr.co.strato.domain.deployment.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface DeploymentRepository extends JpaRepository<DeploymentEntity, Long>, CustomDeploymentRepository{

	public List<DeploymentEntity> findByNamespaceEntity(NamespaceEntity namespace);
}
