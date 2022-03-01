package kr.co.strato.domain.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.work.model.WorkHistoryEntity;

public interface WorkHistoryRepository extends JpaRepository<WorkHistoryEntity, Long> {

}
