package kr.co.strato.domain.cronjob.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cronjob.model.CronJobEntity;

public interface CronJobRepository extends JpaRepository<CronJobEntity, Long>, CustomCronJobRepository{

}
