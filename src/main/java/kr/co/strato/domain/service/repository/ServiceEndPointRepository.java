package kr.co.strato.domain.service.repository;

import kr.co.strato.domain.service.model.ServiceEndpointEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceEndPointRepository extends JpaRepository<ServiceEndpointEntity, Long> {

}
