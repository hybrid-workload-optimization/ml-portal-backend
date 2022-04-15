package kr.co.strato.domain.replicaset.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

public interface ReplicaSetRepository extends JpaRepository<ReplicaSetEntity, Long>, CustomReplicaSetRepository {
	
	List<ReplicaSetEntity> getByDeployment(DeploymentEntity deployment);
	
	@Transactional
	public Integer deleteByNamespace(NamespaceEntity namespaceEntity);
}
