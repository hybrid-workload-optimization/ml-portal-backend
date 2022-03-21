package kr.co.strato.domain.job.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.model.QJobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomJobRepositoryImpl  implements CustomJobRepository{
	private final JPAQueryFactory jpaQueryFactory;

    public CustomJobRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public JobEntity findByUidAndNamespaceIdx(String jobUid, NamespaceEntity namespaceEntity){
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QJobEntity qJobEntity = QJobEntity.jobEntity;
        
        // required condition
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qNamespaceEntity.id.eq(namespaceEntity.getId()));
        builder.and(qJobEntity.jobUid.eq(jobUid));

        JobEntity results =
                jpaQueryFactory
                        .select(qJobEntity)
                        .from(qJobEntity)
                        .leftJoin(qJobEntity.namespaceEntity, qNamespaceEntity)
                        .where(builder)
                        .orderBy(qJobEntity.jobIdx.desc())
                        .fetchOne();

		return results;
    }
}
