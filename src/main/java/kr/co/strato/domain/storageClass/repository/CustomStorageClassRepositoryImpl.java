package kr.co.strato.domain.storageClass.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.storageClass.model.QStorageClassEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;

public class CustomStorageClassRepositoryImpl implements CustomStorageClassRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomStorageClassRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<StorageClassEntity> getStorageClassList(Pageable pageable, Long clusterId,String name) {

        QStorageClassEntity qStorageClassEntity = QStorageClassEntity.storageClassEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }

        QueryResults<StorageClassEntity> results =
                jpaQueryFactory
                        .select(qStorageClassEntity)
                        .from(qStorageClassEntity)
                        .leftJoin(qStorageClassEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qStorageClassEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<StorageClassEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public StorageClassEntity findByName(String name) {
		QStorageClassEntity qStorageClassEntity = QStorageClassEntity.storageClassEntity;
		BooleanBuilder builder = new BooleanBuilder();
       
		StorageClassEntity result = jpaQueryFactory
                .select(qStorageClassEntity)
                .from(qStorageClassEntity)
                .where(builder)
                .orderBy(qStorageClassEntity.id.desc())
                .fetchOne();
		return result;
	}




}
