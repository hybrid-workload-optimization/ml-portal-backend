package kr.co.strato.domain.cronjob.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.domain.cronjob.model.QCronJobEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.portal.workload.model.CronJobArgDto;

public class CustomCronJobRepositoryImpl  implements CustomCronJobRepository{
	private final JPAQueryFactory jpaQueryFactory;

    public CustomCronJobRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<CronJobEntity> getPageList(Pageable pageable, CronJobArgDto args) {
    	QCronJobEntity qCronJobEntity = QCronJobEntity.cronJobEntity;

        BooleanBuilder builder = new BooleanBuilder();
//	    Long projectIdx= args.getProjectIdx();
//	    if(projectIdx != null && projectIdx > 0L){
        
//	    }

//	    Long clusterIdx= args.getClusterIdx();
//	    if(clusterIdx != null && clusterIdx > 0L){
        
//	    }
        
        Long namespaceIdx = args.getNamespaceIdx();
        if(namespaceIdx != null && namespaceIdx > 0L){
            builder.and(QNamespaceEntity.namespaceEntity.id.eq(namespaceIdx));
        }

		QueryResults<CronJobEntity> results = jpaQueryFactory
				.selectFrom(qCronJobEntity)
				.where(builder)
				.orderBy(qCronJobEntity.cronJobIdx.desc())
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize()).fetchResults();

        List<CronJobEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
