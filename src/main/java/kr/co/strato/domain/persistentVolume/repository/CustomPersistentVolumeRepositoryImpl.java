package kr.co.strato.domain.persistentVolume.repository;

import static kr.co.strato.domain.project.model.QProjectEntity.projectEntity;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.persistentVolume.model.QPersistentVolumeEntity;
import kr.co.strato.domain.storageClass.model.QStorageClassEntity;

public class CustomPersistentVolumeRepositoryImpl implements CustomPersistentVolumeRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public CustomPersistentVolumeRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	@Override
	public Page<PersistentVolumeEntity> getPersistentVolumeList(Pageable pageable, Long clusterId, String name) {

		QPersistentVolumeEntity qPersistentVolumeEntity = QPersistentVolumeEntity.persistentVolumeEntity;
		QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
		QStorageClassEntity qStorageClassEntity = QStorageClassEntity.storageClassEntity;

		BooleanBuilder builder = new BooleanBuilder();
		if (clusterId != null && clusterId > 0L) {
			builder.and(qClusterEntity.clusterIdx.eq(clusterId));
		}

		QueryResults<PersistentVolumeEntity> results = jpaQueryFactory.select(qPersistentVolumeEntity)
				.from(qPersistentVolumeEntity).leftJoin(qPersistentVolumeEntity.cluster, qClusterEntity)
				.leftJoin(qPersistentVolumeEntity.storageClass, qStorageClassEntity).where(builder)
				.orderBy(qPersistentVolumeEntity.id.desc()).offset(pageable.getOffset()).limit(pageable.getPageSize())
				.fetchResults();

		List<PersistentVolumeEntity> content = results.getResults();
		long total = results.getTotal();

		return new PageImpl<>(content, pageable, total);
	}

	@Override
	public List<PersistentVolumeEntity> findByStorageClassIdx(Long storageClassIdx) {
		QPersistentVolumeEntity qPersistentVolumeEntity = QPersistentVolumeEntity.persistentVolumeEntity;
		BooleanBuilder builder = new BooleanBuilder();

		QueryResults<PersistentVolumeEntity> results = jpaQueryFactory.select(qPersistentVolumeEntity)
				.from(qPersistentVolumeEntity).where(builder).orderBy(qPersistentVolumeEntity.id.desc()).fetchResults();

		List<PersistentVolumeEntity> content = results.getResults();
		return new ArrayList<>(content);
	}

	@Override
	public PersistentVolumeEntity findPersistentVolumeDetail(Long id) {
		QPersistentVolumeEntity qPersistentVolumeEntity = QPersistentVolumeEntity.persistentVolumeEntity;
		QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
		QStorageClassEntity qStorageClassEntity = QStorageClassEntity.storageClassEntity;
		BooleanBuilder builder = new BooleanBuilder();
		 if(id != null) {
		    	builder.and(qPersistentVolumeEntity.id.eq(id));
		    }
		PersistentVolumeEntity result = jpaQueryFactory.select(qPersistentVolumeEntity)
				.from(qPersistentVolumeEntity).leftJoin(qPersistentVolumeEntity.cluster, qClusterEntity)
				.leftJoin(qPersistentVolumeEntity.storageClass, qStorageClassEntity).where(builder)
				.fetchOne();

		
		return result;
	}

}
