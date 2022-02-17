package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.cluster.model.ClusterEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterDtoMapper {

	ClusterDtoMapper INSTANCE = Mappers.getMapper(ClusterDtoMapper.class);
	
	public ClusterEntity toEntity(ClusterDto dto);
	
	public ClusterDto toDto(ClusterEntity cluster);
	
}
