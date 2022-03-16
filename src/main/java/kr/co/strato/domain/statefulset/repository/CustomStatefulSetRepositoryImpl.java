package kr.co.strato.domain.statefulset.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.statefulset.model.QStatefulSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;

public class CustomStatefulSetRepositoryImpl implements CustomStatefulSetRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomStatefulSetRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<StatefulSetEntity> getStatefulSetList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {

        QStatefulSetEntity qStatefulSetEntity = QStatefulSetEntity.statefulSetEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(projectId != null && projectId > 0L){
            //TODO 프로젝트 조회 조건 추가 필요
        }
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }
        if(namespaceId != null && namespaceId > 0L){
            builder.and(qNamespaceEntity.id.eq(namespaceId));
        }

        QueryResults<StatefulSetEntity> results =
                jpaQueryFactory
                        .select(qStatefulSetEntity)
                        .from(qStatefulSetEntity)
                        .leftJoin(qStatefulSetEntity.namespace, qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qStatefulSetEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<StatefulSetEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
