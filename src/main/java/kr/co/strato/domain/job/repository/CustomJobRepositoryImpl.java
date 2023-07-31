package kr.co.strato.domain.job.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.model.QJobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.portal.workload.v1.model.JobArgDto;

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
    
	 @Override
    public Page<JobEntity> getPageList(Pageable pageable, JobArgDto args) {
    	QJobEntity qJobEntity = QJobEntity.jobEntity;
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

		QueryResults<JobEntity> results = jpaQueryFactory
				.selectFrom(qJobEntity)
				.leftJoin(qJobEntity.namespaceEntity, qNamespaceEntity)
	            .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
				.where(builder)
				.orderBy(qJobEntity.jobIdx.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize()).fetchResults();

        List<JobEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public JobEntity getJob(Long clusterIdx, String namespace, String name) {
		QJobEntity qEntity = QJobEntity.jobEntity;
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.jobName.eq(name));	
		builder.and(qNamespaceEntity.name.eq(namespace));
		builder.and(qNamespaceEntity.cluster.clusterIdx.eq(clusterIdx));
		
		JobEntity results = jpaQueryFactory
				.selectFrom(qEntity)
                .join(qNamespaceEntity).on(qNamespaceEntity.id.eq(qEntity.namespaceEntity.id))
				.where(builder)
				.fetchOne();
		
		return results;
	}

}
