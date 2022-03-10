package kr.co.strato.domain.node.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.node.model.QNodeEntity;

public class CustomNodeRepositoryImpl implements CustomNodeRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomNodeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<NodeEntity> getNodeList(Pageable pageable, Long clusterId,String name) {

        QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }

        QueryResults<NodeEntity> results =
                jpaQueryFactory
                        .select(qNodeEntity)
                        .from(qNodeEntity)
                        .leftJoin(qNodeEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNodeEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<NodeEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public Page<NodeEntity> findByClusterIdx(ClusterEntity clusterEntity, Pageable pageable) {
		QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;
		QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;

		BooleanBuilder builder = new BooleanBuilder();
		if (clusterEntity.getClusterIdx() != null && clusterEntity.getClusterIdx() > 0L) {
			builder.and(qClusterEntity.clusterIdx.eq(clusterEntity.getClusterIdx()));
		}

		QueryResults<NodeEntity> results = jpaQueryFactory.select(qNodeEntity).from(qNodeEntity)
				.leftJoin(qNodeEntity.cluster, qClusterEntity).where(builder).orderBy(qNodeEntity.id.desc())
				.offset(pageable.getOffset()).limit(pageable.getPageSize()).fetchResults();

		List<NodeEntity> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}
}
