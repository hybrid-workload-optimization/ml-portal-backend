package kr.co.strato.domain.namespace.repository;

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

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.cronjob.model.QCronJobEntity;
import kr.co.strato.domain.deployment.model.QDeploymentEntity;
import kr.co.strato.domain.ingress.model.QIngressEntity;
import kr.co.strato.domain.ingress.model.QIngressRuleEntity;
import kr.co.strato.domain.job.model.QJobEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
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

public class CustomNamespaceRepositoryImpl implements CustomNamespaceRepository{
    private final JPAQueryFactory jpaQueryFactory;

    public CustomNamespaceRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

    @Override
    public Page<NamespaceEntity> getNamespaceList(Pageable pageable, Long clusterId,String name) {

        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }

        QueryResults<NamespaceEntity> results =
                jpaQueryFactory
                        .select(qNamespaceEntity)
                        .from(qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNamespaceEntity.id.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<NamespaceEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }

	@Override
	public List<NamespaceEntity> findByNameAndClusterIdx(String name, ClusterEntity clusterEntity) {
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
        
        // required condition
        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qClusterEntity.clusterIdx.eq(clusterEntity.getClusterIdx()));
        builder.and(qNamespaceEntity.name.eq(name));

        QueryResults<NamespaceEntity> results =
                jpaQueryFactory
                        .select(qNamespaceEntity)
                        .from(qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNamespaceEntity.id.desc())
                        .fetchResults();

        List<NamespaceEntity> content = results.getResults();
		return new ArrayList<>(content);
	}

	@Override
	public List<NamespaceEntity> findByClusterIdx(ClusterEntity clusterIdx) {
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(clusterIdx.getClusterIdx() != null && clusterIdx.getClusterIdx() > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterIdx.getClusterIdx()));
        }

        QueryResults<NamespaceEntity> results =
                jpaQueryFactory
                        .select(qNamespaceEntity)
                        .from(qNamespaceEntity)
                        .leftJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qNamespaceEntity.id.desc())
                        .fetchResults();

        List<NamespaceEntity> content = results.getResults();
		return new ArrayList<>(content);
	}
	
	
	@Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteNamespace(Long namespaceId) {
        QPodStatefulSetEntity podStatefulSetEntity = QPodStatefulSetEntity.podStatefulSetEntity;
        QPodReplicaSetEntity podReplicaSetEntity = QPodReplicaSetEntity.podReplicaSetEntity;
        QPodJobEntity podJobEntity = QPodJobEntity.podJobEntity;
        QPodPersistentVolumeClaimEntity podPersistentVolumeClaimEntity = QPodPersistentVolumeClaimEntity.podPersistentVolumeClaimEntity;
        QPodEntity pod = QPodEntity.podEntity;
        QJobEntity job = QJobEntity.jobEntity;
        QCronJobEntity cronJob = QCronJobEntity.cronJobEntity;
        QIngressRuleEntity ingressRule = QIngressRuleEntity.ingressRuleEntity;
        QIngressEntity ingress = QIngressEntity.ingressEntity;
        QPersistentVolumeClaimEntity persistentVolumeClaim = QPersistentVolumeClaimEntity.persistentVolumeClaimEntity;
        QReplicaSetEntity replicaSet = QReplicaSetEntity.replicaSetEntity;
        QDeploymentEntity deployment = QDeploymentEntity.deploymentEntity;
        QServiceEndpointEntity serviceEndpoint = QServiceEndpointEntity.serviceEndpointEntity;
        QServiceEntity service = QServiceEntity.serviceEntity;
        QStatefulSetEntity statefulSet = QStatefulSetEntity.statefulSetEntity;
        QNamespaceEntity namespace = QNamespaceEntity.namespaceEntity;

        //pod_replica_set 
        jpaQueryFactory.delete(podReplicaSetEntity)
        .where(podReplicaSetEntity.pod.eq(
                JPAExpressions
                        .select(pod)
                        .from(pod)
                        .join(pod.namespace, namespace)
                        .where(namespace.id.eq(namespaceId))
        ))
        .execute();

        //pod_stateful_set
        jpaQueryFactory.delete(podStatefulSetEntity)
                .where(podStatefulSetEntity.statefulSet.eq(
                        JPAExpressions
                                .select(statefulSet)
                                .from(statefulSet)
                                .join(statefulSet.namespace, namespace)
                                .where(namespace.id.eq(namespaceId))
                ))
                .execute();

        //pod_daemon_set 

        //pod_job 
        jpaQueryFactory.delete(podJobEntity)
        .where(podJobEntity.pod.eq(
                JPAExpressions
                        .select(pod)
                        .from(pod)
                        .join(pod.namespace, namespace)
                        .where(namespace.id.eq(namespaceId))
        ))
        .execute();
        

        //pod_persistent_volume_claim
        jpaQueryFactory.delete(podPersistentVolumeClaimEntity)
                .where(podPersistentVolumeClaimEntity.persistentVolumeClaim.eq(
                        JPAExpressions
                                .select(persistentVolumeClaim)
                                .from(persistentVolumeClaim)
                                .join(persistentVolumeClaim.namespace, namespace)
                                .where(namespace.id.eq(namespaceId))
                ))
                .execute();

        //pod
        jpaQueryFactory.delete(pod)
                .where(pod.namespace.id.eq(namespaceId))
                .execute();

        //job
        jpaQueryFactory.delete(job)
                .where(job.namespaceEntity.id.eq(namespaceId))
                .execute();

        //cron_job
        jpaQueryFactory.delete(cronJob)
                .where(cronJob.namespaceEntity.id.eq(namespaceId))
                .execute();

        //config_map


        //daemon_set


        //ingress_rule
        jpaQueryFactory.delete(ingressRule)
                .where(ingressRule.ingress.eq(
                        JPAExpressions
                                .select(ingress)
                                .from(ingress)
                                .join(ingress.namespace, namespace)
                                .where(namespace.id.eq(namespaceId))
                ))
                .execute();

        //ingress
        jpaQueryFactory.delete(ingress)
                .where(ingress.namespace.id.eq(namespaceId))
                .execute();

        //persistent_volume_claim
        jpaQueryFactory.delete(persistentVolumeClaim)
                .where(persistentVolumeClaim.namespace.id.eq(namespaceId))
                .execute();

        //secret

        //replica_set
        jpaQueryFactory.delete(replicaSet)
                .where(replicaSet.namespace.id.eq(namespaceId))
                .execute();

        //deployment
        jpaQueryFactory.delete(deployment)
                .where(deployment.namespaceEntity.id.eq(namespaceId))
                .execute();

        //service_endpoint
        jpaQueryFactory.delete(serviceEndpoint)
                .where(serviceEndpoint.service.eq(
                        JPAExpressions
                                .select(service)
                                .from(service)
                                .join(service.namespace, namespace)
                                .where(namespace.id.eq(namespaceId))
                ))
                .execute();

        //service
        jpaQueryFactory.delete(service)
                .where(service.namespace.id.eq(namespaceId))
                .execute();

        //stateful_set
        jpaQueryFactory.delete(statefulSet)
                .where(statefulSet.namespace.id.eq(namespaceId))
                .execute();

        //namespace
        jpaQueryFactory.delete(namespace)
                .where(namespace.id.eq(namespaceId))
                .execute();
    }


}
