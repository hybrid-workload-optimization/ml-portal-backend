package kr.co.strato.domain.service.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.service.model.ServiceEntity;

public interface CustomServiceRepository {
    public Page<ServiceEntity> getServices(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx);
    public ServiceEntity getService(Long clusterIdx, String namespace, String name);
}
