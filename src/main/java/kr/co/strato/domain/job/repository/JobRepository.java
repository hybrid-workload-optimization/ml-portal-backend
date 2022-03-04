package kr.co.strato.domain.job.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.job.model.JobEntity;

public interface JobRepository extends JpaRepository<JobEntity, Long>, CustomJobRepository{

}
