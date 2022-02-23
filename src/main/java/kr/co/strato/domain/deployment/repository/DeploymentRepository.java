package kr.co.strato.domain.deployment.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import kr.co.strato.domain.deployment.model.DeploymentEntity;

public interface DeploymentRepository extends JpaRepository<DeploymentEntity, Long>, JpaSpecificationExecutor<DeploymentEntity>{

}
