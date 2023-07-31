package kr.co.strato.domain.deployment.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.portal.workload.v1.model.DeploymentArgDto;

public interface CustomDeploymentRepository {
	public Page<DeploymentEntity> getDeploymentPageList(Pageable pageable, DeploymentArgDto args);
	public DeploymentEntity getDeployment(Long clusterIdx, String namespace, String name);
}
