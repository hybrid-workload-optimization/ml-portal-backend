package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.node.model.NodeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterNodeDtoMapper {

	ClusterNodeDtoMapper INSTANCE = Mappers.getMapper(ClusterNodeDtoMapper.class);
	
//	public Node toEntity(ClusterNodeDto dto);
	
	@Mapping(target = "clusterIdx" , source = "clusterIdx.clusterIdx")
	public ClusterNodeDto toDto(NodeEntity node);
}
