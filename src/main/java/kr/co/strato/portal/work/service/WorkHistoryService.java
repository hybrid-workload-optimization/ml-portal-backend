package kr.co.strato.portal.work.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.work.model.WorkHistoryEntity;
import kr.co.strato.domain.work.service.WorkHistoryDomainService;
import kr.co.strato.portal.work.model.WorkHistoryDto;
import kr.co.strato.portal.work.model.WorkHistoryDtoMapper;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class WorkHistoryService {

	@Autowired
	WorkHistoryDomainService workHistoryDomainService; 
	
	/**
	 * 작업 이력 등록
	 * 
	 * @param workHistoryDto
	 * @return
	 */
	public Long registerWorkHistory(WorkHistoryDto workHistoryDto) {
		log.debug("registerWorkHistory = {}", workHistoryDto.toString());
		
		WorkHistoryEntity workHistoryEntity = WorkHistoryDtoMapper.INSTANCE.toEntity(workHistoryDto);
		
		workHistoryDomainService.register(workHistoryEntity);
		
		return workHistoryEntity.getWorkHistoryIdx();
	}
}
