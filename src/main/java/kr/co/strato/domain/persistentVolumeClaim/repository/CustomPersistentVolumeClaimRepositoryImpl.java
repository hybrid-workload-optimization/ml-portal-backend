package kr.co.strato.domain.persistentVolumeClaim.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.QPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.QPodEntity;
import kr.co.strato.domain.pod.model.QPodPersistentVolumeClaimEntity;

public class CustomPersistentVolumeClaimRepositoryImpl implements CustomPersistentVolumeClaimRepository {
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomPersistentVolumeClaimRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	public List<PersistentVolumeClaimEntity> findByPod(Long podId) {
		QPersistentVolumeClaimEntity qPersistentVolumeClaimEntity = QPersistentVolumeClaimEntity.persistentVolumeClaimEntity;
		QPodPersistentVolumeClaimEntity qPodPersistentVolumeClaimEntity = QPodPersistentVolumeClaimEntity.podPersistentVolumeClaimEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
        if(podId != null){
        	builder.and(qPodPersistentVolumeClaimEntity.pod.id.eq(podId));
        }
		
		QueryResults<PersistentVolumeClaimEntity> results =
                jpaQueryFactory
                        .select(qPersistentVolumeClaimEntity)
                        .from(qPodPersistentVolumeClaimEntity)
                        .leftJoin(qPodPersistentVolumeClaimEntity.persistentVolumeClaim, qPersistentVolumeClaimEntity)
                        .where(builder)
                        .fetchResults();
		
		List<PersistentVolumeClaimEntity> content = results.getResults();
		
		return content;
	}
}
