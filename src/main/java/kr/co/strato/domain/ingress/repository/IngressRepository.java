package kr.co.strato.domain.ingress.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.ingress.model.IngressEntity;

public interface IngressRepository extends JpaRepository<IngressEntity, Long> ,CustomIngressRepository {
}
