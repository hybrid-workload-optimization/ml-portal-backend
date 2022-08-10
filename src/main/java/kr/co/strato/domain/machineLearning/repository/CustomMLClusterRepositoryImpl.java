package kr.co.strato.domain.machineLearning.repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
}
