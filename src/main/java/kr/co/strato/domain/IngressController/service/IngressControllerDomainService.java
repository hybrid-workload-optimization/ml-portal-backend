package kr.co.strato.domain.IngressController.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.IngressController.repository.IngressControllerRepository;
import kr.co.strato.domain.cluster.model.ClusterEntity;

@Service
public class IngressControllerDomainService {
	
	@Autowired
	private IngressControllerRepository ingressControllerRepository;
	
	/**
	 * IngressControllerEntity 반환.
	 * @param ingressControllerIdx
	 * @return
	 */
	public IngressControllerEntity getIngressControllerById(Long ingressControllerIdx) {
		Optional<IngressControllerEntity> op = ingressControllerRepository.findById(ingressControllerIdx);
		if(op.isPresent()) {
			return op.get();
		}
		return null;
	}
	
	/**
	 * Default 컨트롤러가 존재하는지 리턴.
	 * @param entity
	 * @return
	 */
	public boolean isExistDefaultController(ClusterEntity entity) {
		List<IngressControllerEntity> list = ingressControllerRepository.findByClusterAndDefaultYn(entity, "Y");
		return list.size() > 0;
	}
	
	public Page<IngressControllerEntity> getList(Pageable pageable, Long clusterIdx) {
		return ingressControllerRepository.getList(pageable, clusterIdx);
	}
	
	public List<IngressControllerEntity> getList(ClusterEntity entity) {
		return ingressControllerRepository.findByCluster(entity);
	}
	
	public void deleteById(Long ingressControllerIdx) {
		ingressControllerRepository.deleteById(ingressControllerIdx);
	}
	
	public Long registry(IngressControllerEntity entity) {
		ingressControllerRepository.save(entity);
		return entity.getId();
	}
	
	public Long update(IngressControllerEntity entity) {
		ingressControllerRepository.save(entity);
		return entity.getId();
	}

}
