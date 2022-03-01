package kr.co.strato.domain.deployment.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import kr.co.strato.domain.deployment.model.QDeploymentEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.portal.workload.model.DeploymentArgDto;

public class CustomDeploymentRepositoryImpl  implements CustomDeploymentRepository{
	private final JPAQueryFactory jpaQueryFactory;

    public CustomDeploymentRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<DeploymentEntity> getDeploymentPageList(Pageable pageable, DeploymentArgDto args) {
    	QDeploymentEntity qDeploymentEntity = QDeploymentEntity.deploymentEntity;

        BooleanBuilder builder = new BooleanBuilder();
//        Long projectIdx= args.getProjectIdx();
//        if(projectIdx != null && projectIdx > 0L){
//        }
        
//        Long clusterIdx= args.getClusterIdx();
//        if(clusterIdx != null && clusterIdx > 0L){
//        }
        
        Long namespaceIdx = args.getNamespaceIdx();
        if(namespaceIdx != null && namespaceIdx > 0L){
            builder.and(QNamespaceEntity.namespaceEntity.id.eq(namespaceIdx));
        }

		QueryResults<DeploymentEntity> results = jpaQueryFactory
				.selectFrom(qDeploymentEntity)
				.where(builder)
				.orderBy(qDeploymentEntity.deploymentIdx.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize()).fetchResults();

        List<DeploymentEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
