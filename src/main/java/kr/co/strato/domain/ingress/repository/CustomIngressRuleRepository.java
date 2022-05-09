package kr.co.strato.domain.ingress.repository;

import java.util.List;

import kr.co.strato.domain.ingress.model.IngressRuleEntity;

public interface CustomIngressRuleRepository {
	List<IngressRuleEntity> findByIngressId(Long id);
    public void deleteIngressRule(Long ingressId);
    
    public List<String> getIngressPath(Long clusterIdx);
    
}
