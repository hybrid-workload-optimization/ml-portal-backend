package kr.co.strato.domain.service.repository;

import kr.co.strato.domain.service.model.ServiceEndpointEntity;
import kr.co.strato.domain.service.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ServiceEndPointRepository extends JpaRepository<ServiceEndpointEntity, Long> {
    List<ServiceEndpointEntity> findByService(ServiceEntity service);

    void deleteByService(ServiceEntity service);
}
