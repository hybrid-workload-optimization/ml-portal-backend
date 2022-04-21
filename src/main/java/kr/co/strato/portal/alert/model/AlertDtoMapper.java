package kr.co.strato.portal.alert.model;

import org.mapstruct.Mapper;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.alert.model.AlertEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AlertDtoMapper {
	AlertDtoMapper INSTANCE = Mappers.getMapper(AlertDtoMapper.class);

	@Named("toDto")
	public AlertDto toDto(AlertEntity entity);

	@Named("toEntity")
	public AlertEntity toEntity(AlertDto dto);
}
