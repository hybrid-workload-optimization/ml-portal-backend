package kr.co.strato.portal.ml.v1.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
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
	
	@Mapping(target = "mlStep",				source = "entity.mlStepCode",			qualifiedByName = "getMlStep")
	public MLDto.ListDtoForPortal toListDtoForPortal(MLEntity entity);
	
	@Mapping(target = "mlStep",				source = "entity.mlStepCode",			qualifiedByName = "getMlStep")
	public MLDto.DetailForPortal toDetailDtoForPortal(MLEntity entity);
	
	
	@Mapping(target = "name", source = "mlResName")
	public MLResourceDto toResDto(MLResourceEntity entity);
	
	
	@Named("getMlStep")
    default String getMlStep(String code) {
		String mlStep = null;
		if(code != null) {
			MLStepCode stepCode = MLStepCode.getByCode(code);			
			mlStep = stepCode.name();
		}
		return mlStep;
    }
}
