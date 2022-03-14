package kr.co.strato.domain.statefulset.service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.repository.StatefulSetRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StatefulSetDomainService {
    @Autowired
    private StatefulSetRepository statefulSetRepository;


    @Autowired
    private NamespaceRepository namespaceRepository;


    public Long register(StatefulSetEntity statefulSet, ClusterEntity cluster, String namespaceName) {
        statefulSet.setNamespace(getNamespace(cluster, namespaceName));
        statefulSetRepository.save(statefulSet);
        return statefulSet.getId();
    }

    /**
     * 스테이트풀셋 전체 업데이트(id, namespace 제외)
     * @param statefulSetId 업데이트 될 엔티티의 아이디
     * @param updateEntity 업데이트 할 새로운 데이터 엔티티
     * @return
     */
    public Long update(Long statefulSetId, StatefulSetEntity updateEntity) {
        StatefulSetEntity oldEntity = get(statefulSetId);
        changeToNewData(oldEntity, updateEntity);
        statefulSetRepository.save(oldEntity);
        return oldEntity.getId();
    }

    public boolean delete(Long statefulSetId){
        Optional<StatefulSetEntity> opt = statefulSetRepository.findById(statefulSetId);
        if(opt.isPresent()){
            StatefulSetEntity entity = opt.get();
            statefulSetRepository.delete(entity);
        }
        return true;
    }

    public Page<StatefulSetEntity> getStatefulSets(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {
        return statefulSetRepository.getStatefulSetList(pageable, projectId, clusterId, namespaceId);
    }

    public ClusterEntity getClusterEntity(Long statefulSetId){
        StatefulSetEntity entity = get(statefulSetId);

        return entity.getNamespace().getClusterIdx();
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
