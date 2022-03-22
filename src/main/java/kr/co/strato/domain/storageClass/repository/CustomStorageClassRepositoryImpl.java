package kr.co.strato.domain.storageClass.repository;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.cronjob.model.QCronJobEntity;
import kr.co.strato.domain.deployment.model.QDeploymentEntity;
import kr.co.strato.domain.ingress.model.QIngressEntity;
import kr.co.strato.domain.ingress.model.QIngressRuleEntity;
import kr.co.strato.domain.job.model.QJobEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.persistentVolume.model.QPersistentVolumeEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.QPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.QPodEntity;
import kr.co.strato.domain.pod.model.QPodJobEntity;
import kr.co.strato.domain.pod.model.QPodPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.QPodReplicaSetEntity;
import kr.co.strato.domain.pod.model.QPodStatefulSetEntity;
import kr.co.strato.domain.replicaset.model.QReplicaSetEntity;
import kr.co.strato.domain.service.model.QServiceEndpointEntity;
import kr.co.strato.domain.service.model.QServiceEntity;
import kr.co.strato.domain.statefulset.model.QStatefulSetEntity;
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
		if(name != null && !name.equals("")){
            builder.and(qStorageClassEntity.name.eq(name));
        }
		StorageClassEntity result = jpaQueryFactory
                .select(qStorageClassEntity)
                .from(qStorageClassEntity)
                .where(builder)
                .orderBy(qStorageClassEntity.id.desc())
                .fetchOne();
		return result;
	}

	
	@Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteStorageClass(Long storageClassId) {
		QStorageClassEntity qStorageClassEntity = QStorageClassEntity.storageClassEntity;
        QPersistentVolumeEntity persistentVolumeEntity = QPersistentVolumeEntity.persistentVolumeEntity;
        
      //persistent_volume
        jpaQueryFactory.update(persistentVolumeEntity)
                .where(persistentVolumeEntity.storageClass.id.eq(storageClassId))
                .setNull(persistentVolumeEntity.storageClass)
                .execute();
        
        //storage_class
        jpaQueryFactory.delete(qStorageClassEntity)
                .where(qStorageClassEntity.id.eq(storageClassId))
                .execute();
    }




}
