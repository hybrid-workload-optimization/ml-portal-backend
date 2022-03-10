package kr.co.strato.portal.setting.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.domain.setting.model.SettingEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ToolsDtoMapper {
	ToolsDtoMapper INSTANCE = Mappers.getMapper(ToolsDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "idx",      target = "settingIdx"),
		@Mapping(source = "type",     target = "settingType"),
		@Mapping(source = "key",      target = "settingKey"),
		@Mapping(source = "value",    target = "settingValue")
	})
	@Named(value = "toolsDtoByReqViewDtoToEntity")
	public SettingEntity toEntityByReqViewDto(ToolsDto.ReqViewDto dto);
	
	@Mappings({
		@Mapping(source = "type",     target = "settingType"),
		@Mapping(source = "key",      target = "settingKey"),
		@Mapping(source = "value",    target = "settingValue")
	})
	public SettingEntity toEntityByReqRegistDto(ToolsDto.ReqRegistDto dto);
	
	@Mappings({
		@Mapping(source = "idx",      target = "settingIdx"),
		@Mapping(source = "type",     target = "settingType"),
		@Mapping(source = "key",      target = "settingKey"),
		@Mapping(source = "value",    target = "settingValue")
	})
	public SettingEntity toEntityByReqModifyDto(ToolsDto.ReqModifyDto dto);
	
	@Mappings({
		@Mapping(source = "settingIdx",      target = "idx"),
		@Mapping(source = "settingType",     target = "type"),
		@Mapping(source = "settingKey",      target = "key"),
		@Mapping(source = "settingValue",    target = "value")
	})
	public ToolsDto.ViewDto toViewDto(SettingEntity entity);
	
	@IterableMapping(qualifiedByName = "toolsDtoByReqViewDtoToEntity")
	public List<SettingEntity> toEntityListByReqViewDto(List<ToolsDto.ReqViewDto> dtoList);
	
	@Named("jsonToMap")
    default List<HashMap<String, Object>> jsonArrayToMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            List<HashMap<String, Object>> list = mapper.readValue(json, new TypeReference<List<HashMap<String, Object>>>(){});
            
            return list;
        } catch (JsonProcessingException e) {
        	e.printStackTrace();
            return new ArrayList<HashMap<String, Object>>();
        }
    }
}
