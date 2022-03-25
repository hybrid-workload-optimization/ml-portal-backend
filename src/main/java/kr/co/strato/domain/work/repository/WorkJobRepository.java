package kr.co.strato.domain.work.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.work.model.WorkJobEntity;

public interface WorkJobRepository extends JpaRepository<WorkJobEntity, Long> {

	public Optional<WorkJobEntity> findByWorkJobTypeAndWorkJobReferenceIdx(String workJobType, Long referenceIdx);
	
}
