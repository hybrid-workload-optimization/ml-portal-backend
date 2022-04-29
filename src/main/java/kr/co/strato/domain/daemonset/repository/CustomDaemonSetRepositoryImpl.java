package kr.co.strato.domain.daemonset.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.daemonset.model.QDaemonSetEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomDaemonSetRepositoryImpl implements CustomDaemonSetRepository {

	private final JPAQueryFactory jpaQueryFactory;

    public CustomDaemonSetRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	@Override
	public Page<DaemonSetEntity> getDaemonSetList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		QDaemonSetEntity qDaemonSetEntity = QDaemonSetEntity.daemonSetEntity;
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

        QueryResults<DaemonSetEntity> results = jpaQueryFactory
        		.select(qDaemonSetEntity)
                .from(qDaemonSetEntity)
                .leftJoin(qDaemonSetEntity.namespace, qNamespaceEntity)
                .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                .where(builder)
                .orderBy(qDaemonSetEntity.daemonSetIdx.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<DaemonSetEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
	}

	@Override
	public DaemonSetEntity getDaemonSet(Long clusterIdx, String namespace, String name) {
		QDaemonSetEntity qEntity = QDaemonSetEntity.daemonSetEntity;
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.daemonSetName.eq(name));	
		builder.and(qNamespaceEntity.name.eq(namespace));
		builder.and(qNamespaceEntity.cluster.clusterIdx.eq(clusterIdx));
		
		DaemonSetEntity results = jpaQueryFactory
				.selectFrom(qEntity)
                .join(qNamespaceEntity).on(qNamespaceEntity.id.eq(qEntity.namespace.id))
				.where(builder)
				.fetchOne();
		
		return results;
	}
}