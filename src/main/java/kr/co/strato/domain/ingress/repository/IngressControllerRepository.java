package kr.co.strato.domain.ingress.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.ingress.model.IngressControllerEntity;

public interface IngressControllerRepository extends JpaRepository<IngressControllerEntity, Long>  {
	IngressControllerEntity findByName(String name);
} 
