package kr.co.strato.domain.statefulset.service;

import javassist.NotFoundException;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
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
    private ClusterRepository clusterRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;


    public Long register(StatefulSetEntity statefulSet, Long clusterId, String namespaceName) {
        addNamespace(statefulSet, clusterId, namespaceName);
        statefulSetRepository.save(statefulSet);
        return statefulSet.getId();
    }

    public StatefulSetEntity get(Long id){
        StatefulSetEntity statefulSet = statefulSetRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourceException("statefulSet id:"+id));

        return statefulSet;
    }

    public boolean delete(Long id){
        Optional<StatefulSetEntity> opt = statefulSetRepository.findById(id);
        if(opt.isPresent()){
            StatefulSetEntity entity = opt.get();
            statefulSetRepository.delete(entity);
        }
        return true;
    }

    public Page<StatefulSetEntity> getStatefulSets(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {
        return statefulSetRepository.getStatefulSetList(pageable, projectId, clusterId, namespaceId);
    }

    private void addNamespace(StatefulSetEntity statefulSet, Long clusterId, String namespaceName){
        Optional<ClusterEntity> optCluster = clusterRepository.findById(clusterId.longValue());

        if(!optCluster.isPresent()){
            ClusterEntity cluster = optCluster.get();
            List<NamespaceEntity> namespaces = namespaceRepository.findByNameAndClusterIdx(namespaceName, cluster);

            if(namespaces != null && namespaces.size() > 0){
                statefulSet.setNamespace(namespaces.get(0));
            }
        }
    }
}
