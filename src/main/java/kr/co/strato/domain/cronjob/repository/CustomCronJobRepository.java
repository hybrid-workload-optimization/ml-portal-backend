package kr.co.strato.domain.cronjob.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.cronjob.model.CronJobEntity;
import kr.co.strato.portal.workload.v1.model.CronJobArgDto;

public interface CustomCronJobRepository {
	Page<CronJobEntity> getPageList(Pageable pageable, CronJobArgDto args);
	public CronJobEntity getCronJob(Long clusterIdx, String namespace, String name);
}
