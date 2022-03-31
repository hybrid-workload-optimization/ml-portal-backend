package kr.co.strato.domain.node.repository;

import java.util.ArrayList;
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
    public Page<NodeEntity> getNodeList(Pageable pageable, Long clusterIdx,String name) {

        QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterIdx != null && clusterIdx > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterIdx));
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
	
	@Override
	public List<NodeEntity> findByNameAndClusterIdx(String name, ClusterEntity clusterEntity) {
		QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
        
        // required condition
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qClusterEntity.clusterIdx.eq(clusterEntity.getClusterIdx()));
        builder.and(qNodeEntity.name.eq(name));

        QueryResults<NodeEntity> results =
                jpaQueryFactory
                        .select(qNodeEntity)
                        .from(qNodeEntity)
                        .leftJoin(qNodeEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNodeEntity.id.desc())
                        .fetchResults();

        List<NodeEntity> content = results.getResults();
		return new ArrayList<>(content);
	}
	
	@Override
	public NodeEntity findNodeName(Long clusterIdx, String name) {
		QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
        
        // required condition
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qClusterEntity.clusterIdx.eq(clusterIdx));
        builder.and(qNodeEntity.name.eq(name));

        NodeEntity results =
                jpaQueryFactory
                        .select(qNodeEntity)
                        .from(qNodeEntity)
                        .leftJoin(qNodeEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNodeEntity.id.desc())
                        .fetchOne();

		return results;
	}
	
}
