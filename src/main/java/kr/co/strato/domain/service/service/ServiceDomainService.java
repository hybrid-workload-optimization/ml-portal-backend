package kr.co.strato.domain.service.service;

import kr.co.strato.domain.service.model.ServiceEntity;
import kr.co.strato.domain.service.repository.ServiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class ServiceDomainService {

    @Autowired
    private ServiceRepository serviceRepository;

    public Page<ServiceEntity> getServices(Pageable pageable, Long projectIdx, Long clusterIdx, Long namespaceIdx) {
        return serviceRepository.getServices(pageable, projectIdx, clusterIdx, namespaceIdx);
    }
}
