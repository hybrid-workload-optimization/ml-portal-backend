package kr.co.strato.domain.IngressController.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;

public interface IngressControllerRepository extends JpaRepository<IngressControllerEntity, Long>, CustomIngressControllerRepository  {
	
	public IngressControllerEntity findByName(String name);
	
	public IngressControllerEntity findByDefaultYn(String defaultYn);
	
} 
