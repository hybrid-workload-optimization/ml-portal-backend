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
	public Page<ReplicaSetEntity> getReplicaSetList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {
		QReplicaSetEntity qReplicaSetEntity = QReplicaSetEntity.replicaSetEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();
        // TODO : projectId/clusterId 검색 조건에 대한 방향 설정이 필요할듯함.
        if (projectId != null && projectId > 0L) {
            // nothing to do
        }
        if (clusterId != null && clusterId > 0L) {
            //builder.and(qClusterEntity.clusterId.eq(clusterId));
        }
        if (namespaceId != null && namespaceId > 0L) {
            builder.and(qNamespaceEntity.id.eq(namespaceId));
        }

        QueryResults<ReplicaSetEntity> results = jpaQueryFactory
        		.select(qReplicaSetEntity)
                .from(qReplicaSetEntity)
                .leftJoin(qReplicaSetEntity.namespace, qNamespaceEntity)
                //.innerJoin(qNamespaceEntity.clusterIdx, qClusterEntity)
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
