package kr.co.strato.portal.setting.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.portal.setting.model.GeneralDto;
import kr.co.strato.portal.setting.model.GeneralDtoMapper;
import kr.co.strato.domain.setting.model.SettingEntity;
import kr.co.strato.domain.setting.service.SettingDomainService;

@Service
public class GeneralService {
	@Autowired
	SettingDomainService settingDomainService;
	
	public List<GeneralDto> getList(){
		List<SettingEntity> entities = settingDomainService.findSettingEntitiesByType("GENERAL");
		List<GeneralDto> dtos = entities.stream().map(GeneralDtoMapper.INSTANCE::toDto).collect(Collectors.toList());
		return dtos;
	}
	
	public GeneralDto get(Long idx){
		SettingEntity entitiy = settingDomainService.getSetting(idx);
		GeneralDto dto = GeneralDtoMapper.INSTANCE.toDto(entitiy);
		return dto;
	}
	
	public void save(GeneralDto dto){
		SettingEntity entity = GeneralDtoMapper.INSTANCE.toEntity(dto);
		settingDomainService.saveSetting(entity);
	}
	
	public void delete(Long idx){
		settingDomainService.deleteSettingByIdx(idx);
	}
}
