package kr.co.strato.domain.configMap.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.configMap.model.QConfigMapEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomConfigMapRepositoryImpl implements CustomConfigMapRepository {

	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomConfigMapRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	@Override
	public Page<ConfigMapEntity> getConfigMapList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		QConfigMapEntity qConfigMapEntity = QConfigMapEntity.configMapEntity;
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

        QueryResults<ConfigMapEntity> results = jpaQueryFactory
        		.select(qConfigMapEntity)
                .from(qConfigMapEntity)
                .leftJoin(qConfigMapEntity.namespace, qNamespaceEntity)
                .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                .where(builder)
                .orderBy(qConfigMapEntity.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<ConfigMapEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
	}

	@Override
	public ConfigMapEntity getConfigMap(Long clusterIdx, String namespace, String name) {
		QConfigMapEntity qEntity = QConfigMapEntity.configMapEntity;
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
		
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.name.eq(name));	
		builder.and(qNamespaceEntity.name.eq(namespace));
		builder.and(qNamespaceEntity.cluster.clusterIdx.eq(clusterIdx));
		
		ConfigMapEntity results = jpaQueryFactory
				.selectFrom(qEntity)
                .join(qNamespaceEntity).on(qNamespaceEntity.id.eq(qEntity.namespace.id))
				.where(builder)
				.fetchOne();
		
		return results;
	}
}
