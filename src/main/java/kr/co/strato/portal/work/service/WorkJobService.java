package kr.co.strato.portal.work.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.work.model.WorkJobEntity;
import kr.co.strato.domain.work.service.WorkJobDomainService;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.work.model.WorkJobDto;
import kr.co.strato.portal.work.model.WorkJobDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkJobService {

	@Autowired
	WorkJobDomainService workJobDomainService;
	
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
		
		return workJobEntity.getWorkJobIdx();
	}
	
	public Long updateWorkJob(WorkJobDto workJobDto) {
		log.debug("updateWorkJob = {}", workJobDto.toString());
		
		WorkJobEntity workJobEntity = WorkJobDtoMapper.INSTANCE.toEntity(workJobDto);
		
		workJobDomainService.update(workJobEntity);
		
		return workJobEntity.getWorkJobIdx();
	}
	
}
