package kr.co.strato.portal.setting.service;


import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.kubespray.service.KubesprayAdapterService;
import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.service.SettingDomainService;
import kr.co.strato.portal.setting.model.SettingSelectorDto;
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
		
		List<SettingSelectorDto> convertKubesprayVersions = new ArrayList<>();
		
		for (String v : kubesprayVersions ) {
			SettingSelectorDto selector = new SettingSelectorDto(v, v, v);
			convertKubesprayVersions.add(selector);
		}
		
		if ( ObjectUtils.isEmpty(returnDto) ) returnDto = new ToolsDto();
		returnDto.setKubesprayVersions(convertKubesprayVersions);
		return returnDto;
	}
	
	/** 
	 * tools 생성
	 * @param GeneralDto dto
	 * @return GeneralDto dto
	 */
	public Long postTools(ToolsDto dto) {
		// DTO TO ENTITY
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntity(dto);
		param.setUpdatedAt(new Date());
		
		Long l = settingDomainService.saveSetting(param);
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
		Long l = (long) 0;
		
		if ( ObjectUtils.isEmpty(entity) ) {
			//조회된 엔티티가 없으면 신규저장
			l = postTools(dto);
		}else {
			if ( ObjectUtils.isNotEmpty(param) ) {
				if ( StringUtils.isNotEmpty(param.getSettingKey()) ) entity.setSettingKey(param.getSettingKey());
				if ( StringUtils.isNotEmpty(param.getSettingType()) ) entity.setSettingType(param.getSettingType());
				if ( StringUtils.isNotEmpty(param.getSettingValue()) ) entity.setSettingValue(param.getSettingValue());
				if ( StringUtils.isNotEmpty(param.getDescription()) ) entity.setDescription(param.getDescription());
				entity.setUpdatedAt(new Date());
				
				l = settingDomainService.saveSetting(entity);
			}
		}
		
		return l;
	}
}
