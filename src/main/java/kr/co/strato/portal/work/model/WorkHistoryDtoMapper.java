package kr.co.strato.portal.work.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.work.model.WorkHistoryEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkHistoryDtoMapper {
	
	WorkHistoryDtoMapper INSTANCE = Mappers.getMapper(WorkHistoryDtoMapper.class);
	
	public WorkHistoryEntity toEntity(WorkHistoryDto workHistoryDto);
	
	public WorkHistoryDto toDto(WorkHistoryEntity workHistoryEntity);
	
}
