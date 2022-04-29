package kr.co.strato.domain.secret.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.secret.model.QSecretEntity;
import kr.co.strato.domain.secret.model.SecretEntity;

public class CustomSecretRepositoryImpl implements CustomSecretRepository {

	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomSecretRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	@Override
	public Page<SecretEntity> getSecretList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		QSecretEntity qSecretEntity = QSecretEntity.secretEntity;
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

        QueryResults<SecretEntity> results = jpaQueryFactory
        		.select(qSecretEntity)
                .from(qSecretEntity)
                .leftJoin(qSecretEntity.namespace, qNamespaceEntity)
                .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                .where(builder)
                .orderBy(qSecretEntity.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<SecretEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
	}

	@Override
	public SecretEntity getSecret(Long clusterIdx, String namespace, String name) {
		QSecretEntity qEntity = QSecretEntity.secretEntity;
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.name.eq(name));	
		builder.and(qNamespaceEntity.name.eq(namespace));
		builder.and(qNamespaceEntity.cluster.clusterIdx.eq(clusterIdx));
		
		SecretEntity results = jpaQueryFactory
				.selectFrom(qEntity)
                .join(qNamespaceEntity).on(qNamespaceEntity.id.eq(qEntity.namespace.id))
				.where(builder)
				.fetchOne();
		
		return results;
	}
}
