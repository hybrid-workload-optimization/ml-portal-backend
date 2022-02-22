package kr.co.strato.portal.setting.service;


import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.kubespray.service.KubesprayAdapterService;
import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.service.SettingDomainService;
import kr.co.strato.portal.setting.model.ToolsDto;
import kr.co.strato.portal.setting.model.ToolsDtoMapper;

@Service
public class ToolsService {
	
	@Autowired
	private SettingDomainService settingDomainService;
	
	@Autowired
	private KubesprayAdapterService kubesprayAdapterService;
	
	/** 
	 * tools 조회
	 * @param rtMap 
	 * @param ToolsDto dto
	 * @return ToolsDto dto
	 */
	public ToolsDto getTools(ToolsDto dto) {
		// DTO TO ENTITY
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntity(dto);
		// GET REAL ENTITY (BY PARAM ENTITY)
		SettingEntity entity = settingDomainService.getSetting(param);
		// REAL ENTITY TO DTO
		ToolsDto returnDto = ToolsDtoMapper.INSTANCE.toDto(entity);
		
		// GET kubespray version (api call)
		List<String> kubesprayVersions = kubesprayAdapterService.getVersion();
		returnDto.setKubesprayVersions(kubesprayVersions);
		
		return returnDto;
	}
	
	/** 
	 * tools 생성(일단 엔티티 조회한 뒤, 데이터 없으면 생성. 있으면 patch 처리)
	 * @param GeneralDto dto
	 * @return GeneralDto dto
	 */
	public Long postTools(ToolsDto dto) {
		// DTO TO ENTITY
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntity(dto);
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
	public Long patchTools(ToolsDto dto) {
		// DTO TO ENTITY
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntity(dto);
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
