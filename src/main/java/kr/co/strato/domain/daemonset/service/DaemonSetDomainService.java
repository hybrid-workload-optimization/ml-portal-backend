package kr.co.strato.domain.daemonset.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.common.model.ResourceType;
import kr.co.strato.domain.common.service.InNamespaceDomainService;
import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.daemonset.repository.DaemonSetRepository;
import kr.co.strato.domain.pod.repository.PodRepository;
import kr.co.strato.global.error.exception.NotFoundResourceException;

@Service
public class DaemonSetDomainService implements InNamespaceDomainService {

	@Autowired
	DaemonSetRepository daemonSetRepository;
	
	@Autowired
	PodRepository podRepository;
	
	public Long register(DaemonSetEntity daemonSetEntity) {
		daemonSetRepository.save(daemonSetEntity);
		
		return daemonSetEntity.getDaemonSetIdx();
	}
	
	public Page<DaemonSetEntity> getList(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
        return daemonSetRepository.getDaemonSetList(pageable, projectIdx, clusterIdx, namespaceIdx);
    }
	
	public DaemonSetEntity get(Long daemonSetIdx) {
		Optional<DaemonSetEntity> daemonSet = daemonSetRepository.findById(daemonSetIdx);
		if (daemonSet.isPresent()) {
			return daemonSet.get();
		} else {
			throw new NotFoundResourceException("Daemon : " + daemonSet.toString());
		}
	}
	
	public void delete(DaemonSetEntity daemonSetEntity) {
		//파드 삭제
		podRepository.deleteByOwnerUidAndKind(daemonSetEntity.getDaemonSetUid(), ResourceType.daemonSet.get());
		daemonSetRepository.delete(daemonSetEntity);
	}

	@Override
	public boolean isDuplicateName(Long clusterIdx, String namespace, String name) {
		Object obj = daemonSetRepository.getDaemonSet(clusterIdx, namespace, name);
		return obj != null;
	}
}
