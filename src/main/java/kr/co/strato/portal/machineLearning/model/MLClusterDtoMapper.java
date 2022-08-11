package kr.co.strato.portal.machineLearning.model;

import java.util.ArrayList;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.machineLearning.model.MLClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.portal.cluster.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.model.ClusterNodeDtoMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MLClusterDtoMapper {

	MLClusterDtoMapper INSTANCE = Mappers.getMapper(MLClusterDtoMapper.class);
	
	@Mapping(target = "clusterId", 		source = "entity.id")
	@Mapping(target = "clusterName", 	source = "entity.cluster.clusterName")
	@Mapping(target = "description", 	source = "entity.cluster.description")
	@Mapping(target = "status", 	source = "entity.status")
	@Mapping(target = "nodeCount", 	source = "entity.cluster.nodes",	qualifiedByName = "nodeCount")
	@Mapping(target = "provider", 	source = "entity.cluster.provider")
	@Mapping(target = "kubeVersion", 	source = "entity.cluster.providerVersion")
	@Mapping(target = "createdAt", 	source = "entity.createdAt")
	public MLClusterDto.List toListDto(MLClusterEntity entity);
	
	
	@Mapping(target = "clusterId", 		source = "entity.id")
	@Mapping(target = "clusterName", 	source = "entity.cluster.clusterName")
	@Mapping(target = "description", 	source = "entity.cluster.description")
	@Mapping(target = "status", 	source = "entity.status")
	@Mapping(target = "nodeCount", 	source = "entity.cluster.nodes",	qualifiedByName = "nodeCount")
	@Mapping(target = "provider", 	source = "entity.cluster.provider")
	@Mapping(target = "kubeVersion", 	source = "entity.cluster.providerVersion")
	@Mapping(target = "createdAt", 	source = "entity.createdAt")
	@Mapping(target = "nodes", 	source = "entity.cluster.nodes", qualifiedByName = "toNodeDtos")
	public MLClusterDto.Detail toDetailDto(MLClusterEntity entity);
	
	
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
