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
	public ToolsDto.ViewDto getTools(ToolsDto.ReqViewDto dto) {
		// DTO TO ENTITY
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntityByReqViewDto(dto);
		// GET REAL ENTITY (BY PARAM ENTITY)
		SettingEntity entity = settingDomainService.getSetting(param);
		// REAL ENTITY TO DTO
		ToolsDto.ViewDto returnDto = ToolsDtoMapper.INSTANCE.toViewDto(entity);
		
		// GET kubespray version (api call)
		List<String> kubesprayVersions = kubesprayAdapterService.getVersion();
		
		List<SettingSelectorDto> convertKubesprayVersions = new ArrayList<>();
		
		for (String v : kubesprayVersions ) {
			SettingSelectorDto selector = new SettingSelectorDto(v, v, v);
			convertKubesprayVersions.add(selector);
		}
		
		if ( ObjectUtils.isEmpty(returnDto) ) returnDto = new ToolsDto.ViewDto();
		returnDto.setKubesprayVersions(convertKubesprayVersions);
		return returnDto;
	}
	
	/** 
	 * tools 생성
	 * @param GeneralDto dto
	 * @return GeneralDto dto
	 */
	public Long postTools(ToolsDto.ReqRegistDto dto) {
		// DTO TO ENTITY
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntityByReqRegistDto(dto);
		param.setUpdatedAt(new Date());
		
		Long l = settingDomainService.saveSetting(param);
		return l;
	}
	
	/** 
	 * tools 수정
	 * @param GeneralDto dto
	 * @return Long id
	 */
	public Long patchTools(ToolsDto.ReqModifyDto dto) {
		System.out.println("####dto :: " + dto.toString());
		
		// DTO TO ENTITY
		dto.setKey(dto.getKey().toUpperCase()); //사전 약속으로 대문자 세팅
		SettingEntity param = ToolsDtoMapper.INSTANCE.toEntityByReqModifyDto(dto);
		// GET REAL ENTITY (BY PARAM ENTITY)
		SettingEntity entity = settingDomainService.getSetting(param);
		Long l = (long) 0;
		
		if ( ObjectUtils.isEmpty(entity) ) {
			//조회된 엔티티가 없으면 신규저장
			ToolsDto.ReqRegistDto reqDto = new ToolsDto.ReqRegistDto();
			reqDto.setType(dto.getType());
			reqDto.setKey(dto.getKey());
			reqDto.setValue(dto.getValue());
			reqDto.setDescription(dto.getDescription());
			l = postTools(reqDto);
		}else {
			if ( ObjectUtils.isNotEmpty(param) ) {
				if ( StringUtils.isNotEmpty(param.getSettingKey()) ) entity.setSettingKey(param.getSettingKey());
				if ( StringUtils.isNotEmpty(param.getSettingType()) ) entity.setSettingType(param.getSettingType());
				if ( StringUtils.isNotEmpty(param.getSettingValue()) ) entity.setSettingValue(param.getSettingValue());
				if ( StringUtils.isNotEmpty(param.getDescription()) ) entity.setDescription(param.getDescription());
				entity.setUpdatedAt(new Date());
				
				l = settingDomainService.saveSetting(entity);
			}
			
			modifyToolsAdvanced(dto.getSetting());
		}
		
		return l;
	}
	
	public void modifyToolsAdvanced(HashMap<String, String> advancedData) {
		System.out.println("####advancedData :: " + advancedData.toString());
		
	}

	public List<HashMap<String, Object>> getToolsAdvanced(String version) {
		String setting = kubesprayAdapterService.getSetting(version);
		List<HashMap<String, Object>> settingMapList = ToolsDtoMapper.INSTANCE.jsonArrayToMap(setting);
		
		settingMapList.stream().forEach( sObj -> {
			if ( ObjectUtils.isNotEmpty(sObj.get("option")) ) {
				List<String> optionList = (List<String>) sObj.get("option");
				List<SettingSelectorDto> optionSelectList = new ArrayList<>();
				for ( String o : optionList ) {
					SettingSelectorDto dto = new SettingSelectorDto(o, o, o);
					optionSelectList.add(dto);
				}
				sObj.put("optionList", optionSelectList);
			}else {
				sObj.put("optionList", null);
			}
		});
		
		return settingMapList;
	}
}
