package kr.co.strato.domain.deployment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.model.QDeploymentEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.portal.workload.v1.model.DeploymentArgDto;

public class CustomDeploymentRepositoryImpl  implements CustomDeploymentRepository{
	private final JPAQueryFactory jpaQueryFactory;

    public CustomDeploymentRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<DeploymentEntity> getDeploymentPageList(Pageable pageable, DeploymentArgDto args) {
    	QDeploymentEntity qDeploymentEntity = QDeploymentEntity.deploymentEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();

        Long clusterIdx= args.getClusterIdx();
        if(clusterIdx != null && clusterIdx > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterIdx));
        }
        Long namespaceIdx = args.getNamespaceIdx();
        if(namespaceIdx != null && namespaceIdx > 0L){
            builder.and(QNamespaceEntity.namespaceEntity.id.eq(namespaceIdx));
        }

		QueryResults<DeploymentEntity> results = jpaQueryFactory
				.selectFrom(qDeploymentEntity)
                .leftJoin(qDeploymentEntity.namespaceEntity, qNamespaceEntity)
                .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
				.where(builder)
				.orderBy(qDeploymentEntity.deploymentIdx.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize()).fetchResults();

        List<DeploymentEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public DeploymentEntity getDeployment(Long clusterIdx, String namespace, String name) {
		QDeploymentEntity qDeploymentEntity = QDeploymentEntity.deploymentEntity;
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qDeploymentEntity.deploymentName.eq(name));	
		builder.and(qNamespaceEntity.name.eq(namespace));
		builder.and(qNamespaceEntity.cluster.clusterIdx.eq(clusterIdx));
		
		DeploymentEntity results = jpaQueryFactory
				.selectFrom(qDeploymentEntity)
                .join(qNamespaceEntity).on(qNamespaceEntity.id.eq(qDeploymentEntity.namespaceEntity.id))
				.where(builder)
				.fetchOne();
		
		return results;
	}
}
