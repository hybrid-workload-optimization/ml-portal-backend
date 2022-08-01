package kr.co.strato.portal.machineLearning.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.MLResourceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MLDtoMapper {

	MLDtoMapper INSTANCE = Mappers.getMapper(MLDtoMapper.class);
	
	public MLEntity toEntity(MLDto.ApplyArg dto);
	
	
	public MLDto.Detail toDetailDto(MLEntity entity);
	
	public MLDto.ListDto toListDto(MLEntity entity);
	
	
	@Mapping(target = "name", source = "mlResName")
	public MLResourceDto toResDto(MLResourceEntity entity);
	
	
}
