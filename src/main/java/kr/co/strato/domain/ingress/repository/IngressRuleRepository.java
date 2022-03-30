package kr.co.strato.domain.ingress.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.ingress.model.IngressRuleEntity;

public interface IngressRuleRepository extends JpaRepository<IngressRuleEntity, Long>,CustomIngressRuleRepository  {
}
