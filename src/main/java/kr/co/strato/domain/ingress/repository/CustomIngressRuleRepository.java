package kr.co.strato.domain.ingress.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;

public interface CustomIngressRuleRepository {
	List<IngressRuleEntity> findByIngressId(Long id);
    public void deleteIngressRule(Long ingressId);
    
}
