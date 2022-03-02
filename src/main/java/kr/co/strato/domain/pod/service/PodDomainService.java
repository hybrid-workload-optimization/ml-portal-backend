package kr.co.strato.domain.pod.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.pod.repository.PodRepository;

@Service
public class PodDomainService {
    @Autowired
    private PodRepository podRepository;

    public Page<PodEntity> getPods(Pageable pageable, Long projectId, Long clusterId, Long namespaceId) {
        return podRepository.getPodList(pageable, projectId, clusterId, namespaceId);
    }

}
