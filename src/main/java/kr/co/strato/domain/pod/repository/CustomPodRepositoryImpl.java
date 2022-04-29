package kr.co.strato.domain.pod.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.job.model.QJobEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.node.model.QNodeEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.QPodEntity;
import kr.co.strato.domain.pod.model.QPodJobEntity;
import kr.co.strato.domain.pod.model.QPodReplicaSetEntity;
import kr.co.strato.domain.pod.model.QPodStatefulSetEntity;
import kr.co.strato.domain.replicaset.model.QReplicaSetEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.statefulset.model.QStatefulSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

public class CustomPodRepositoryImpl implements CustomPodRepository {
	private final JPAQueryFactory jpaQueryFactory;
	
	public CustomPodRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }
	
	
	@Override
    public Page<PodEntity> getPodList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId, Long nodeId) {

        QPodEntity qPodEntity = QPodEntity.podEntity;
        QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
        QClusterEntity qClusterEntity = QClusterEntity.clusterEntity;
        QNodeEntity qNodeEntity = QNodeEntity.nodeEntity;


        BooleanBuilder builder = new BooleanBuilder();
        if(projectId != null && projectId > 0L){
            //TODO 프로젝트 조회 조건 추가 필요
        }
        if(clusterId != null && clusterId > 0L){
            builder.and(qClusterEntity.clusterIdx.eq(clusterId));
        }
        if(namespaceId != null && namespaceId > 0L){
            builder.and(qNamespaceEntity.id.eq(namespaceId));
        }
        if(nodeId != null && nodeId > 0L){
            builder.and(qNodeEntity.id.eq(nodeId));
        }

        QueryResults<PodEntity> results =
                jpaQueryFactory
                        .select(qPodEntity)
                        .from(qPodEntity)
                        .leftJoin(qPodEntity.namespace, qNamespaceEntity)
                        .leftJoin(qPodEntity.node, qNodeEntity)
                        .innerJoin(qNamespaceEntity.cluster, qClusterEntity)
                        .where(builder)
                        .orderBy(qPodEntity.createdAt.desc())
                        .offset(pageable.getOffset())
                        .limit(pageable.getPageSize())
                        .fetchResults();

        List<PodEntity> content = results.getResults();
        long total = results.getTotal();

        return new PageImpl<>(content, pageable, total);
    }
	
	@Override
	public StatefulSetEntity getPodStatefulSet(Long podId) {
		QPodStatefulSetEntity qPodStatefulSetEntity = QPodStatefulSetEntity.podStatefulSetEntity;
		QStatefulSetEntity qStatefulSetEntity = QStatefulSetEntity.statefulSetEntity;
		QPodEntity qPodEntity = QPodEntity.podEntity;

		StatefulSetEntity result = 
				jpaQueryFactory
						.select(qStatefulSetEntity)
						.from(qStatefulSetEntity)
						.leftJoin(qStatefulSetEntity.podStatefulSets, qPodStatefulSetEntity)
						.leftJoin(qPodStatefulSetEntity.pod, qPodEntity)
						.where(qPodEntity.id.eq(podId))
						.fetchOne();
		
		return result;
	}
	
//	@Override
//	public StatefulSetEntity getPodDaemonSet(Long podId) {
//		QPodDaemonSetEntity qPodDaemonSetEntity = QPodDaemonSetEntity.podDaemonSetEntity;
//		QDaemonSetEntity qDaemonSetEntity = QDaemonSetEntity.daemonSetEntity;
//		QPodEntity qPodEntity = QPodEntity.podEntity;
//
//		StatefulSetEntity result = 
//				jpaQueryFactory
//						.select(qStatefulSetEntity)
//						.from(qStatefulSetEntity)
//						.leftJoin(qStatefulSetEntity.podStatefulSets, qPodStatefulSetEntity)
//						.leftJoin(qPodStatefulSetEntity.pod, qPodEntity)
//						.where(qPodEntity.id.eq(podId))
//						.fetchOne();
//		
//		return result;
//	}
	
	@Override
	public ReplicaSetEntity getPodReplicaSet(Long podId) {
		QPodReplicaSetEntity qPodReplicaSetEntity = QPodReplicaSetEntity.podReplicaSetEntity;
		QReplicaSetEntity qReplicaSetEntity = QReplicaSetEntity.replicaSetEntity;
		QPodEntity qPodEntity = QPodEntity.podEntity;

		ReplicaSetEntity result = 
				jpaQueryFactory
						.select(qReplicaSetEntity)
						.from(qReplicaSetEntity)
						.leftJoin(qReplicaSetEntity.podReplicaSets, qPodReplicaSetEntity)
						.leftJoin(qPodReplicaSetEntity.pod, qPodEntity)
						.where(qPodEntity.id.eq(podId))
						.fetchOne();
		
		return result;
	}
	
	@Override
	public JobEntity getPodJob(Long podId) {
		QPodJobEntity qPodJobEntity = QPodJobEntity.podJobEntity;
		QJobEntity qJobEntity = QJobEntity.jobEntity;
		QPodEntity qPodEntity = QPodEntity.podEntity;

		JobEntity result = 
				jpaQueryFactory
						.select(qJobEntity)
						.from(qJobEntity)
						.leftJoin(qJobEntity.podJobs, qPodJobEntity)
						.leftJoin(qPodJobEntity.pod, qPodEntity)
						.where(qPodEntity.id.eq(podId))
						.fetchOne();
		
		return result;
	}
	
	@Override
	public List<PodEntity> findAllByNamespaceIdx(Long namespaceId) {
		QPodEntity qPodEntity = QPodEntity.podEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qPodEntity.namespace.id.eq(namespaceId));
		QueryResults<PodEntity> results =
                jpaQueryFactory
                        .select(qPodEntity)
                        .from(qPodEntity)
                        .where(builder)
                        .fetchResults();
		
		List<PodEntity> content = results.getResults();
		return content;
	}


	@Override
	public PodEntity getPod(Long clusterIdx, String namespace, String name) {
		QPodEntity qEntity = QPodEntity.podEntity;
		QNamespaceEntity qNamespaceEntity = QNamespaceEntity.namespaceEntity;
		 
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qEntity.podName.eq(name));	
		builder.and(qNamespaceEntity.name.eq(namespace));
		builder.and(qNamespaceEntity.cluster.clusterIdx.eq(clusterIdx));
		
		PodEntity results = jpaQueryFactory
				.selectFrom(qEntity)
                .join(qNamespaceEntity).on(qNamespaceEntity.id.eq(qEntity.namespace.id))
				.where(builder)
				.fetchOne();
		
		return results;
	}
	
}
