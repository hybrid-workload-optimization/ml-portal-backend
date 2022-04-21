package kr.co.strato.domain.persistentVolumeClaim.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.QPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.QPodPersistentVolumeClaimEntity;

public class CustomPersistentVolumeClaimRepositoryImpl implements CustomPersistentVolumeClaimRepository {
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomPersistentVolumeClaimRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	@Override
	public Page<PersistentVolumeClaimEntity> getPersistentVolumeClaimList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
		QPersistentVolumeClaimEntity qPersistentVolumeClaimEntity = QPersistentVolumeClaimEntity.persistentVolumeClaimEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;

        BooleanBuilder builder = new BooleanBuilder();
        if (projectIdx != null && projectIdx > 0L) {
            // nothing to do
        }
        if (clusterIdx != null && clusterIdx > 0L) {
        	builder.and(qClusterEntity.clusterIdx.eq(clusterIdx));
        }
        if (namespaceIdx != null && namespaceIdx > 0L) {
            builder.and(qNamespaceEntity.id.eq(namespaceIdx));
        }

        QueryResults<PersistentVolumeClaimEntity> results = jpaQueryFactory
        		.select(qPersistentVolumeClaimEntity)
                .from(qPersistentVolumeClaimEntity)
                .leftJoin(qPersistentVolumeClaimEntity.namespace, qNamespaceEntity)
                .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                .where(builder)
                .orderBy(qPersistentVolumeClaimEntity.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();

        List<PersistentVolumeClaimEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
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
