package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.cluster.model.Cluster;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PortalClusterDtoMapper {

	PortalClusterDtoMapper INSTANCE = Mappers.getMapper(PortalClusterDtoMapper.class);
	
	public Cluster toEntity(PortalClusterDto dto);
	
	public PortalClusterDto toDto(Cluster cluster);
	
}
