package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.namespace.model.NamespaceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterNamespaceDtoMapper {

	ClusterNamespaceDtoMapper INSTANCE = Mappers.getMapper(ClusterNamespaceDtoMapper.class);
	
	@Mapping(target = "clusterIdx" , source = "clusterIdx.clusterIdx")
	public ClusterNamespaceDto toDto(NamespaceEntity node);
}
