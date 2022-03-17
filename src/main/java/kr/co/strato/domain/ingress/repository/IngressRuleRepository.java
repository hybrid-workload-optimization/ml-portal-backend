package kr.co.strato.domain.ingress.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.ingress.model.IngressEntity;
import kr.co.strato.domain.ingress.model.IngressRuleEntity;

public interface IngressRuleRepository extends JpaRepository<IngressRuleEntity, Long>  {
	List<IngressRuleEntity> findByIngressId(Long id);
}
