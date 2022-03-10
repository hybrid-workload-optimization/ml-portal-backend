package kr.co.strato.domain.pod.repository;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.node.model.QNodeEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.QPodEntity;

public class CustomPodRepositoryImpl implements CustomPodRepository {
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomPodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	@Override
    public Page<PodEntity> getPodList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId) {

        QPodEntity qPodEntity = QPodEntity.podEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
        QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(projectId != null && projectId > 0L){
            //TODO 프로젝트 조회 조건 추가 필요
        }
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterId.eq(clusterId));
        }
        if(namespaceId != null && namespaceId > 0L){
            builder.and(qNamespaceEntity.id.eq(namespaceId));
        }
        if(nodeId != null && nodeId > 0L){
            builder.and(qNodeEntity.id.eq(nodeId));
        }

        QueryResults<PodEntity> results =
                jpaQueryFactory
                        .select(qPodEntity)
                        .from(qPodEntity)
                        .leftJoin(qPodEntity.namespace, qNamespaceEntity)
                        .leftJoin(qPodEntity.node, qNodeEntity)
                        .where(builder)
                        .orderBy(qPodEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<PodEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
}
