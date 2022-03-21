package kr.co.strato.domain.pod.repository;

import java.util.Arrays;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.QClusterEntity;
import kr.co.strato.domain.namespace.model.QNamespaceEntity;
import kr.co.strato.domain.node.model.QNodeEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.QPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.model.QPodEntity;
import kr.co.strato.domain.pod.model.QPodPersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.QPodStatefulSetEntity;
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
                        .orderBy(qPodEntity.id.desc())
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
	
//	@Override
//    public void deleteByOwnerUidAndKind(String ownerUid, String kind) {
//		// TODO 테스트 필요
//		QPodEntity qPodEntity = QPodEntity.podEntity;
//		BooleanBuilder builder = new BooleanBuilder();
//		builder.and(qPodEntity.kind.eq(kind));
//		builder.and(qPodEntity.ownerUid.eq(ownerUid));
//		
//		QueryResults<PodEntity> pods = jpaQueryFactory.selectFrom(qPodEntity).where(builder).fetchResults();
//		List<PodEntity> podEntities = pods.getResults();
//		podEntities.stream().forEach(e -> e.removePodStatefulSet());
//
//    	Long result =
//    			jpaQueryFactory
//    				.delete(qPodEntity)
//    				.where(builder)
//    				.execute();
//    }
}
