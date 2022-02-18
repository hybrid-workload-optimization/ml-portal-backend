package kr.co.strato.portal.setting.service;


import java.util.Date;
import java.util.HashMap;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.service.SettingDomainService;
import kr.co.strato.portal.setting.model.GeneralDto;
import kr.co.strato.portal.setting.model.GeneralDtoMapper;

@Service
public class ToolsService {
	
	@Autowired
	private SettingDomainService settingDomainService;
	
	/** 
	 * tools 조회
	 * @param rtMap 
	 * @param GeneralDto dto
	 * @return GeneralDto dto
	 */
	public GeneralDto getTools(GeneralDto dto) {
		// DTO TO ENTITY
		SettingEntity param = GeneralDtoMapper.INSTANCE.toEntity(dto);
		// GET REAL ENTITY (BY PARAM ENTITY)
		SettingEntity entity = settingDomainService.getSetting(param);
		// REAL ENTITY TO DTO
		GeneralDto returnDto = GeneralDtoMapper.INSTANCE.toDto(entity);
		
		return returnDto;
	}
	
	/** 
	 * tools 생성(일단 엔티티 조회한 뒤, 데이터 없으면 생성. 있으면 patch 처리)
	 * @param GeneralDto dto
	 * @return GeneralDto dto
	 */
	public Long postTools(GeneralDto dto) {
		// DTO TO ENTITY
		SettingEntity param = GeneralDtoMapper.INSTANCE.toEntity(dto);
		// GET REAL ENTITY (BY PARAM ENTITY)
		SettingEntity entity = settingDomainService.getSetting(param);
		
		Long l = settingDomainService.saveSetting(entity);
		
		return l;
	}
	
	/** 
	 * tools 수정
	 * @param GeneralDto dto
	 * @return Long id
	 */
	public Long patchTools(GeneralDto dto) {
		// DTO TO ENTITY
		SettingEntity param = GeneralDtoMapper.INSTANCE.toEntity(dto);
		// GET REAL ENTITY (BY PARAM ENTITY)
		SettingEntity entity = settingDomainService.getSetting(param);
		
		if ( ObjectUtils.isNotEmpty(param) ) {
			if ( StringUtils.isNotEmpty(param.getSettingKey()) ) entity.setSettingKey(param.getSettingKey());
			if ( StringUtils.isNotEmpty(param.getSettingType()) ) entity.setSettingType(param.getSettingType());
			if ( StringUtils.isNotEmpty(param.getSettingValue()) ) entity.setSettingValue(param.getSettingValue());
			if ( StringUtils.isNotEmpty(param.getDescription()) ) entity.setDescription(param.getDescription());
		}
		entity.setUpdatedAt(new Date());
		
		Long l = settingDomainService.saveSetting(entity);
		return l;
	}
}
