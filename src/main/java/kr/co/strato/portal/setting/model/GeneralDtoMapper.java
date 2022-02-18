package kr.co.strato.portal.setting.model;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.setting.model.SettingEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GeneralDtoMapper {
	GeneralDtoMapper INSTANCE = Mappers.getMapper(GeneralDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "idx",      target = "settingIdx"),
		@Mapping(source = "type",     target = "settingType"),
		@Mapping(source = "key",      target = "settingKey"),
		@Mapping(source = "value",    target = "settingValue")
	})
	@Named("toSettingEntity")
	public SettingEntity toEntity(GeneralDto dto);
	
	@Mappings({
		@Mapping(source = "settingIdx",      target = "idx"),
		@Mapping(source = "settingType",     target = "type"),
		@Mapping(source = "settingKey",      target = "key"),
		@Mapping(source = "settingValue",    target = "value")
	})
	public GeneralDto toDto(SettingEntity entity);
	
	@IterableMapping(qualifiedByName = "toSettingEntity")
	public List<SettingEntity> toEntityList(List<GeneralDto> dtoList);
}
