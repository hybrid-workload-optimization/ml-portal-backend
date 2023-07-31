package kr.co.strato.portal.ml.model;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDtoMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MLClusterDtoMapper {

	MLClusterDtoMapper INSTANCE = Mappers.getMapper(MLClusterDtoMapper.class);
	
	@Mapping(target = "clusterId", 		source = "entity.clusterIdx")
	@Mapping(target = "clusterName", 	source = "entity.clusterName")
	@Mapping(target = "description", 	source = "entity.description")
	@Mapping(target = "status", 	source = "entity.status")
	@Mapping(target = "nodeCount", 	source = "entity.nodes",	qualifiedByName = "nodeCount")
	@Mapping(target = "provider", 	source = "entity.provider")
	@Mapping(target = "kubeVersion", 	source = "entity.providerVersion")
	@Mapping(target = "createdAt", 	source = "entity.createdAt")
	public MLClusterDto.List toListDto(ClusterEntity entity);
	
	
	@Mapping(target = "clusterId", 		source = "entity.clusterIdx")
	@Mapping(target = "clusterName", 	source = "entity.clusterName")
	@Mapping(target = "description", 	source = "entity.description")
	@Mapping(target = "status", 	source = "entity.status")
	@Mapping(target = "nodeCount", 	source = "entity.nodes",	qualifiedByName = "nodeCount")
	@Mapping(target = "provider", 	source = "entity.provider")
	@Mapping(target = "kubeVersion", 	source = "entity.providerVersion")
	@Mapping(target = "createdAt", 	source = "entity.createdAt")
	@Mapping(target = "nodes", 	source = "entity.nodes", qualifiedByName = "toNodeDtos")
	public MLClusterDto.Detail toDetailDto(ClusterEntity entity);
	
	
	@Named("nodeCount")
    default int node(List<NodeEntity> list) {
        return list.size();
    }
	
	@Named("toNodeDtos")
    default List<ClusterNodeDto.ResDetailDto> toNodeDtos(List<NodeEntity> list) {
		List<ClusterNodeDto.ResDetailDto> result = new ArrayList<>();
		for(NodeEntity entity : list) {
			ClusterNodeDto.ResDetailDto dto = ClusterNodeDtoMapper.INSTANCE.toResDetailDto(entity);
			result.add(dto);
		} 
        return result;
    }
	
}
