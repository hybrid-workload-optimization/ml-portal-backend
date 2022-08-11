package kr.co.strato.domain.machineLearning.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.machineLearning.model.QMLClusterEntity;

public class CustomMLClusterRepositoryImpl implements CustomMLClusterRepository {
	
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomMLClusterRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
	public void deleteByMlClusterIdx(Long mlClusterIdx) {
		QMLClusterEntity qEntity = QMLClusterEntity.mLClusterEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.id.eq(mlClusterIdx));
		
		jpaQueryFactory.delete(qEntity).where(builder).execute();
	}

	@Override
	public Page<MLClusterEntity> findByClusterType(String clusterType, Pageable pageable) {
		QMLClusterEntity qEntity = QMLClusterEntity.mLClusterEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		if(clusterType != null && clusterType.length() > 0) {
			builder.and(qEntity.clusterType.eq(clusterType));
		}
		
		
		QueryResults<MLClusterEntity> results =
                jpaQueryFactory
                        .select(qEntity)
                        .from(qEntity)
                        .where(builder)
                        .orderBy(qEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<MLClusterEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
	}
}
