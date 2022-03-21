package kr.co.strato.domain.cronjob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.portal.workload.model.CronJobArgDto;

public interface CustomCronJobRepository {
	Page<CronJobEntity> getPageList(Pageable pageable, CronJobArgDto args);
}
