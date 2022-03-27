package kr.co.strato.domain.statefulset.service;

import java.util.List;
import java.util.Optional;

import kr.co.strato.global.error.exception.NoArgumentsRequiredForMethod;
import org.apache.catalina.Cluster;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.repository.StatefulSetRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

import javax.xml.stream.events.Namespace;

@Service
public class StatefulSetDomainService {
    @Autowired
    private StatefulSetRepository statefulSetRepository;
    
    @Autowired
    private PodRepository podRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;


    public Long register(StatefulSetEntity statefulSet, Long clusterIdx, String namespaceName) {
        if(clusterIdx == null || clusterIdx == 0L || namespaceName == null){
            throw new NoArgumentsRequiredForMethod("");
        }
        ClusterEntity cluster = ClusterEntity.builder().clusterIdx(clusterIdx).build();
        NamespaceEntity namespace = getNamespace(cluster, namespaceName);
        if(namespace != null){
            throw new NotFoundResourceException("namespace가 존재하지 않습니다.");
        }
        statefulSet.setNamespace(namespace);
        statefulSetRepository.save(statefulSet);
        return statefulSet.getId();
    }

    /**
     * 스테이트풀셋 전체 업데이트(id, namespace 제외)
     * @param statefulSetId 업데이트 될 엔티티의 아이디
     * @param updateEntity 업데이트 할 새로운 데이터 엔티티
     * @return
     */
    //업데이트 시, statefulSetId를 따로 받는 이유는 어플리케이션 서비스 단에서 엔티티에 id를 매핑해야 한다는 사실을 알 필요가 없게 하기 위해서이다.
    public Long update(Long statefulSetId, StatefulSetEntity updateEntity) {
        if(statefulSetId == null || statefulSetId == 0L){
            throw new NoArgumentsRequiredForMethod("");
        }
        StatefulSetEntity oldEntity = get(statefulSetId);
        changeToNewData(oldEntity, updateEntity);
        statefulSetRepository.save(oldEntity);
        return oldEntity.getId();
    }

    public boolean delete(Long statefulSetId){
        if(statefulSetId == null || statefulSetId == 0L){
            throw new NoArgumentsRequiredForMethod("");
        }
        Optional<StatefulSetEntity> opt = statefulSetRepository.findById(statefulSetId);
        if(opt.isPresent()){
            StatefulSetEntity entity = opt.get();
            podRepository.deleteByOwnerUidAndKind(entity.getStatefulSetUid(), ResourceType.statefulSet.get());
            statefulSetRepository.delete(entity);
        }
        return true;
    }

    public Page<StatefulSetEntity> getStatefulSets(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {
        return statefulSetRepository.getStatefulSetList(pageable, projectId, clusterId, namespaceId);
    }

    public ClusterEntity getClusterEntity(Long statefulSetId){
        StatefulSetEntity entity = get(statefulSetId);

        return entity.getNamespace().getCluster();
    }


    public StatefulSetEntity get(Long statefulSetId){
        StatefulSetEntity statefulSetEntity = statefulSetRepository.findById(statefulSetId)
                .orElseThrow(() -> new NotFoundResourceException("statefulSet id:"+statefulSetId));

        return statefulSetEntity;
    }

    private NamespaceEntity getNamespace(ClusterEntity cluster, String namespaceName){
        List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);

        if(namespaces != null && namespaces.size() > 0){
            return namespaces.get(0);
        }
        return null;
    }

    private void changeToNewData(StatefulSetEntity oldEntity, StatefulSetEntity newEntity){
        oldEntity.setStatefulSetUid(newEntity.getStatefulSetUid());
        oldEntity.setCreatedAt(newEntity.getCreatedAt());
        oldEntity.setImage(newEntity.getImage());
        oldEntity.setAnnotation(newEntity.getAnnotation());
        oldEntity.setLabel(newEntity.getLabel());
    }
}
