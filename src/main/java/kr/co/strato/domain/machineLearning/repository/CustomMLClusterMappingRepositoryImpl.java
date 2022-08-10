package kr.co.strato.domain.machineLearning.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.machineLearning.model.MLClusterMappingEntity;
import kr.co.strato.domain.machineLearning.model.QMLClusterMappingEntity;

public class CustomMLClusterMappingRepositoryImpl implements CustomMLClusterMappingRepository {
	
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomMLClusterMappingRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
	public List<MLClusterMappingEntity> findByMlClusterIdx(Long mlClusterIdx) {
		QMLClusterMappingEntity qEntity = QMLClusterMappingEntity.mLClusterMappingEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.mlCluster.id.eq(mlClusterIdx));

        QueryResults<MLClusterMappingEntity> results = jpaQueryFactory
        		.select(qEntity)
                .from(qEntity)
                .where(builder)
                .orderBy(qEntity.id.desc())
                .fetchResults();

        List<MLClusterMappingEntity> content = results.getResults();
		return content;
	}

	@Override
	public List<MLClusterMappingEntity> findByMlIdx(Long mlIdx) {
		QMLClusterMappingEntity qEntity = QMLClusterMappingEntity.mLClusterMappingEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.ml.id.eq(mlIdx));

		QueryResults<MLClusterMappingEntity> results = jpaQueryFactory
        		.select(qEntity)
                .from(qEntity)
                .where(builder)
                .orderBy(qEntity.id.desc())
                .fetchResults();

        List<MLClusterMappingEntity> content = results.getResults();
		return content;
	}

	@Override
	public void deleteByMlClusterIdx(Long mlClusterIdx) {
		QMLClusterMappingEntity qEntity = QMLClusterMappingEntity.mLClusterMappingEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.mlCluster.id.eq(mlClusterIdx));
		
		jpaQueryFactory.delete(qEntity).where(builder);
	}

	@Override
	public void deleteByMlIdx(Long mlIdx) {
		QMLClusterMappingEntity qEntity = QMLClusterMappingEntity.mLClusterMappingEntity;		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.ml.id.eq(mlIdx));
		jpaQueryFactory.delete(qEntity).where(builder).execute();
	}

}
