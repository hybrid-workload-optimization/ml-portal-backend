package kr.co.strato.domain.namespace.repository;

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
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;

public class CustomNamespaceRepositoryImpl implements CustomNamespaceRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomNamespaceRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<NamespaceEntity> getNamespaceList(Pageable pageable, Long clusterId,String name) {

        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }

        QueryResults<NamespaceEntity> results =
                jpaQueryFactory
                        .select(qNamespaceEntity)
                        .from(qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNamespaceEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<NamespaceEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public List<NamespaceEntity> findByNameAndClusterIdx(String name, ClusterEntity clusterEntity) {
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
        
        // required condition
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qClusterEntity.clusterIdx.eq(clusterEntity.getClusterId()));
        builder.and(qNamespaceEntity.name.eq(name));

        QueryResults<NamespaceEntity> results =
                jpaQueryFactory
                        .select(qNamespaceEntity)
                        .from(qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNamespaceEntity.id.desc())
                        .fetchResults();

        List<NamespaceEntity> content = results.getResults();
		return new ArrayList<>(content);
	}

	@Override
	public List<NamespaceEntity> findByClusterIdx(ClusterEntity clusterIdx) {
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterIdx.getClusterId() != null && clusterIdx.getClusterId() > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterIdx.getClusterId()));
        }

        QueryResults<NamespaceEntity> results =
                jpaQueryFactory
                        .select(qNamespaceEntity)
                        .from(qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNamespaceEntity.id.desc())
                        .fetchResults();

        List<NamespaceEntity> content = results.getResults();
		return new ArrayList<>(content);
	}


}
