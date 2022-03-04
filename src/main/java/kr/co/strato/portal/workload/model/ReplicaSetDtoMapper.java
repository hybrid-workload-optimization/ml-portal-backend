package kr.co.strato.portal.workload.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplicaSetDtoMapper {

	ReplicaSetDtoMapper INSTANCE = Mappers.getMapper(ReplicaSetDtoMapper.class);
	
	public ReplicaSetEntity toEntity(ReplicaSetDto replicaSetDto);
	
	public ReplicaSetDto toDto(ReplicaSetEntity replicaSetEntity);
	
}
