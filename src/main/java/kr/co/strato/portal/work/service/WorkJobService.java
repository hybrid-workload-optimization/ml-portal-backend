package kr.co.strato.portal.work.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.service.WorkJobDomainService;
import kr.co.strato.portal.alert.service.AlertService;
import kr.co.strato.portal.work.model.WorkJobDto;
import kr.co.strato.portal.work.model.WorkJobDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkJobService {

	@Autowired
	WorkJobDomainService workJobDomainService;
	
	@Autowired
	AlertService alertService;
	
	/**
	 * 작업 등록
	 * 
	 * @param workJobDto
	 * @return
	 */
	public Long registerWorkJob(WorkJobDto workJobDto) {
		log.debug("registerWorkJob = {}", workJobDto.toString());
		WorkJobEntity workJobEntity = WorkJobDtoMapper.INSTANCE.toEntity(workJobDto);
		workJobDomainService.register(workJobEntity);
		
		//alert 처리
		alertServiceJob(workJobEntity);
		return workJobEntity.getWorkJobIdx();
	}
	
	/**
	 * 작업 상태 업데이트
	 * @param workJobDto
	 * @return
	 */
	public Long updateWorkJob(WorkJobDto workJobDto) {
		log.debug("updateWorkJob = {}", workJobDto.toString());		
		WorkJobEntity workJobEntity = WorkJobDtoMapper.INSTANCE.toEntity(workJobDto);		
		return updateWorkJob(workJobEntity);
	}
	
	public Long updateWorkJob(WorkJobEntity workJobEntity) {	
		workJobDomainService.update(workJobEntity);
		
		//alert 처리
		alertServiceJob(workJobEntity);
		return workJobEntity.getWorkJobIdx();
	}
	
	
	/**
	 * 작업 반환.
	 * @param workJobId
	 * @return
	 */
	public WorkJobEntity getWorkJob(Long workJobId) {
		return workJobDomainService.get(workJobId);
	}
	
	/**
	 * alert 서비스 처리
	 * @param workJobEntity
	 */
	private void alertServiceJob(WorkJobEntity workJobEntity) {
		try {
			alertService.alertForClusterJob(workJobEntity);
		} catch (Exception e) {
			log.error("", e);
		}
	}
}
