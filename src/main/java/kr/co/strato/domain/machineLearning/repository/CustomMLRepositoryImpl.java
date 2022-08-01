package kr.co.strato.domain.machineLearning.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.QMLEntity;

public class CustomMLRepositoryImpl implements CustomMLRepository {

	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomMLRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
	public List<MLEntity> getMLList(String userId, String name) {
		QMLEntity qEntity = QMLEntity.mLEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
        if (userId != null) {
        	builder.and(qEntity.userId.eq(userId));
        }
        if (name != null) {
        	builder.and(qEntity.name.like(name));
        }

        QueryResults<MLEntity> results = jpaQueryFactory
        		.select(qEntity)
                .from(qEntity)
                .where(builder)
                .orderBy(qEntity.id.desc())
                .fetchResults();

        List<MLEntity> content = results.getResults();
		return content;
	}
}
