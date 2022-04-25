package kr.co.strato.domain.IngressController.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.IngressController.model.IngressControllerEntity;

public interface CustomIngressControllerRepository {

	public Page<IngressControllerEntity> getList(Pageable pageable, Long clusterIdx);
}
