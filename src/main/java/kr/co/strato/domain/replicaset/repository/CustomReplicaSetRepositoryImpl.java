package kr.co.strato.domain.replicaset.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.replicaset.model.QReplicaSetEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

public class CustomReplicaSetRepositoryImpl implements CustomReplicaSetRepository {

	private final JPAQueryFactory jpaQueryFactory;

    public CustomReplicaSetRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
    
	@Override
	public Page<ReplicaSetEntity> getReplicaSetList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		QReplicaSetEntity qReplicaSetEntity = QReplicaSetEntity.replicaSetEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();
        if (projectIdx != null && projectIdx > 0L) {
            // nothing to do
        }
        if (clusterIdx != null && clusterIdx > 0L) {
        	builder.and(qClusterEntity.clusterIdx.eq(clusterIdx));
        }
        if (namespaceIdx != null && namespaceIdx > 0L) {
            builder.and(qNamespaceEntity.id.eq(namespaceIdx));
        }

        QueryResults<ReplicaSetEntity> results = jpaQueryFactory
        		.select(qReplicaSetEntity)
                .from(qReplicaSetEntity)
                .leftJoin(qReplicaSetEntity.namespace, qNamespaceEntity)
                .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                .where(builder)
                .orderBy(qReplicaSetEntity.replicaSetIdx.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ReplicaSetEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
	}

}
