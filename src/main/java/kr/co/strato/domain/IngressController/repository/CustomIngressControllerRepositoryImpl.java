package kr.co.strato.domain.IngressController.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.IngressController.model.QIngressControllerEntity;
import kr.co.strato.domain.cluster.model.QClusterEntity;

public class CustomIngressControllerRepositoryImpl implements CustomIngressControllerRepository {
	
	private final JPAQueryFactory jpaQueryFactory;

    public CustomIngressControllerRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
	public Page<IngressControllerEntity> getList(Pageable pageable, Long clusterIdx) {
		QIngressControllerEntity qIngressControllerEntity = QIngressControllerEntity.ingressControllerEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();
        if(clusterIdx != null && clusterIdx > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterIdx));
        }

        QueryResults<IngressControllerEntity> results =
                jpaQueryFactory
                        .select(qIngressControllerEntity)
                        .from(qIngressControllerEntity)
                        .leftJoin(qIngressControllerEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qIngressControllerEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<IngressControllerEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
	}

}
