package kr.co.strato.domain.statefulset.service;

import javassist.NotFoundException;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.namespace.repository.NamespaceRepository;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.domain.statefulset.repository.StatefulSetRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StatefulSetDomainService {
    @Autowired
    private StatefulSetRepository statefulSetRepository;

    @Autowired
    private ClusterRepository clusterRepository;

    @Autowired
    private NamespaceRepository namespaceRepository;


    public Long registerStatefulSet(StatefulSetEntity statefulSetEntity, Integer clusterId, String namespaceName) {
        Optional<ClusterEntity> opt = clusterRepository.findById(clusterId.longValue());

        if(!opt.isPresent()){
            throw new NotFoundResourceException("해당 클러스터가 존재하지 않습니다:"+clusterId);
        }

        //TODO 클러스터 아이디와 네임스페이스 이름으로 네임스페이스 엔티티 조회 및 statefulSet 객체에 세팅


        statefulSetRepository.save(statefulSetEntity);

        return statefulSetEntity.getId();
    }

    public Page<StatefulSetEntity> getStatefulSetList(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {
        return statefulSetRepository.getStatefulSetList(pageable, projectId, clusterId, namespaceId);
    }
}
