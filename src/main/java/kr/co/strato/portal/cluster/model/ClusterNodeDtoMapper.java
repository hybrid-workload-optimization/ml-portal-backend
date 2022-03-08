package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.portal.workload.model.StatefulSetDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterNodeDtoMapper {

	ClusterNodeDtoMapper INSTANCE = Mappers.getMapper(ClusterNodeDtoMapper.class);
	
	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	 public ClusterNodeDto.ResListDto toResListDto(NodeEntity node);
	
	
	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	 public ClusterNodeDto.ResDetailDto toResDetailDto(NodeEntity node);
	
}
