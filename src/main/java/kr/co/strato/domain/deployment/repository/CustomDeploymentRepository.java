package kr.co.strato.domain.deployment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.portal.workload.model.DeploymentArgDto;

public interface CustomDeploymentRepository {
	public Page<DeploymentEntity> getDeploymentPageList(Pageable pageable, DeploymentArgDto args);
}
