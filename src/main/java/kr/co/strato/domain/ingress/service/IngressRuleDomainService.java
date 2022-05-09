package kr.co.strato.domain.ingress.service;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;
import kr.co.strato.domain.IngressController.repository.IngressControllerRepository;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;
import kr.co.strato.domain.ingress.repository.IngressRuleRepository;
import kr.co.strato.global.error.exception.InternalServerException;

@Service
public class IngressRuleDomainService {
	
	@Autowired
	private IngressRuleRepository ingressRuleRepository;
	
	@Autowired
	private IngressControllerRepository ingressControllerRepository;

	
	public List<IngressRuleEntity> findByIngressId(Long id) {
		return ingressRuleRepository.findByIngressId(id);
	}
	
	public Long register(IngressRuleEntity ingressEntity) {
		ingressRuleRepository.save(ingressEntity);
		return ingressEntity.getId();
	}
	
	public void saveAllingress(List<IngressRuleEntity> params) {
		try {
			ingressRuleRepository.saveAll(params);
		} catch (Throwable e) {
			throw new InternalServerException(e);
		}
	}
	
	public boolean delete(Long id) {
		ingressRuleRepository.deleteIngressRule(id);
		return true;
	}
	

    public Long update(IngressRuleEntity ingressEntity,Long ingressId) {
    	ingressRuleRepository.save(ingressEntity);
		return ingressEntity.getId();
	}
    
    
    public Long getIngressControllerId(String ingressClassName) {
    	IngressControllerEntity ingressControllerEntity  = 	ingressControllerRepository.findByName(ingressClassName);
		return ingressControllerEntity.getId();
	}
    
    /**
     * path 중복 여부 채크.
     * @param clusterIdx
     * @param paths
     * @return
     */
    public boolean duplicateCheckIngressPath(Long clusterIdx, List<String> paths) {
    	List<String> ingressPaths = ingressRuleRepository.getIngressPath(clusterIdx);
    	for(String p : paths) {
    		if(ingressPaths.contains(p)) {
    			return true;
    		}
    	}
    	return false; 
    }
	
}
