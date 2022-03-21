package kr.co.strato.domain.cronjob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.portal.workload.model.CronJobArgDto;

public interface CronJobRepository extends JpaRepository<CronJobEntity, Long>, CustomCronJobRepository{
}
