package kr.co.strato.domain.service.repository;

import kr.co.strato.domain.service.model.ServiceEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ServiceRepository extends JpaRepository<ServiceEntity, Long> {

}
