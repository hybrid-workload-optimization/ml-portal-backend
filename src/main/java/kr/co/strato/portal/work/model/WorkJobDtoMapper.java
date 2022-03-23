package kr.co.strato.portal.work.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.work.model.WorkJobEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkJobDtoMapper {
	
	WorkJobDtoMapper INSTANCE = Mappers.getMapper(WorkJobDtoMapper.class);
	
	public WorkJobEntity toEntity(WorkJobDto workJobDto);
	
	public WorkJobDto toDto(WorkJobEntity workJobEntity);
	
}
