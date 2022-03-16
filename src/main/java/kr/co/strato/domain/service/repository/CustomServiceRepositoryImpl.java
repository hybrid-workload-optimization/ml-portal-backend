package kr.co.strato.domain.service.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.service.model.QServiceEntity;
import kr.co.strato.domain.service.model.ServiceEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CustomServiceRepositoryImpl implements  CustomServiceRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomServiceRepositoryImpl(JPAQueryFactory jpaQueryFactory){
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<ServiceEntity> getServices(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
        QServiceEntity qService = QServiceEntity.serviceEntity;
        QNamespaceEntity qNamespace = QNamespaceEntity.namespaceEntity;
        QClusterEntity qCluster = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();
        if(clusterIdx != null && clusterIdx > 0L){
            builder.and(qCluster.clusterIdx.eq(clusterIdx));
        }
        if(namespaceIdx !=  null && namespaceIdx > 0L){
            builder.and(qNamespace.id.eq(namespaceIdx));
        }

        QueryResults<ServiceEntity> results =
                jpaQueryFactory
                        .select(qService)
                        .from(qService)
                        .leftJoin(qService.namespace, qNamespace)
                        .leftJoin(qNamespace.cluster, qCluster)
                        .where(builder)
                        .orderBy(qService.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<ServiceEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
