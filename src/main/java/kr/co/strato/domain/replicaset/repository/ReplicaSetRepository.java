package kr.co.strato.domain.replicaset.repository;

import java.util.List;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import org.springframework.data.jpa.repository.Query;

public interface ReplicaSetRepository extends JpaRepository<ReplicaSetEntity, Long>, CustomReplicaSetRepository {
	List<ReplicaSetEntity> getByDeployment(DeploymentEntity deployment);
}
