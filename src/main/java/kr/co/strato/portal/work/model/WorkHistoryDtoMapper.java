package kr.co.strato.portal.work.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.work.model.WorkHistoryEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface WorkHistoryDtoMapper {
	
	WorkHistoryDtoMapper INSTANCE = Mappers.getMapper(WorkHistoryDtoMapper.class);
	
	public WorkHistoryEntity toEntity(WorkHistoryDto workHistoryDto);
	
	public WorkHistoryDto toDto(WorkHistoryEntity workHistoryEntity);
	
}
