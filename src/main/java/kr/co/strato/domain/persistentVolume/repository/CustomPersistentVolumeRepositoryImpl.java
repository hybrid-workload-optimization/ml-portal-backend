package kr.co.strato.domain.persistentVolume.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.model.QPersistentVolumeEntity;

public class CustomPersistentVolumeRepositoryImpl implements CustomPersistentVolumeRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomPersistentVolumeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<PersistentVolumeEntity> getPersistentVolumeList(Pageable pageable, Long clusterId,String name) {

        QPersistentVolumeEntity qPersistentVolumeEntity = QPersistentVolumeEntity.persistentVolumeEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }

        QueryResults<PersistentVolumeEntity> results =
                jpaQueryFactory
                        .select(qPersistentVolumeEntity)
                        .from(qPersistentVolumeEntity)
                        .leftJoin(qPersistentVolumeEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qPersistentVolumeEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<PersistentVolumeEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public List<PersistentVolumeEntity> findByStorageClassIdx(Long storageClassIdx) {
		QPersistentVolumeEntity qPersistentVolumeEntity = QPersistentVolumeEntity.persistentVolumeEntity;
		BooleanBuilder builder = new BooleanBuilder();

        QueryResults<PersistentVolumeEntity> results =
                jpaQueryFactory
                        .select(qPersistentVolumeEntity)
                        .from(qPersistentVolumeEntity)
                        .where(builder)
                        .orderBy(qPersistentVolumeEntity.id.desc())
                        .fetchResults();

        List<PersistentVolumeEntity> content = results.getResults();
		return new ArrayList<>(content);
	}




}
