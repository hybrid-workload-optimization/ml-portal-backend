package kr.co.strato.portal.cluster.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.adapter.cloud.cluster.model.ClusterCloudDto;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterDtoMapper {

	ClusterDtoMapper INSTANCE = Mappers.getMapper(ClusterDtoMapper.class);
	
	public ClusterEntity toEntity(ClusterDto.Form dto);
	
	public ClusterDto toDto(ClusterEntity cluster);
	
	@Mapping(target = "nodeCount", 	source = "c.nodes",			qualifiedByName = "nodeCount")
    @Mapping(target = "problem",	source = "c.problem",		qualifiedByName = "jsonToList")
	public ClusterDto.List toList(ClusterEntity c);
	
	@Mapping(target = "kubeConfig",		source = "c.kubeConfig")
	@Mapping(target = "description",	source = "c.description")
    @Mapping(target = "problem",		source = "c.problem",	qualifiedByName = "jsonToList")
	public ClusterDto.Detail toDetail(ClusterEntity c);
	
	@Mapping(target = "nodes",			source = "dto.nodes",	qualifiedByName = "toClusterCloudNodeList")
	public ClusterCloudDto toClusterCloudDto(ClusterDto.Form dto);
	
	@Named("nodeCount")
    default int nodeCount(List<NodeEntity> nodes) {
        return nodes.size();
    }
	
	@Named("jsonToMap")
    default HashMap<String, Object> jsonToMap(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, HashMap.class);
		} catch (Exception e) {
			return new HashMap<>();
		}
    }
	
	@Named("jsonToList")
    default ArrayList<String> jsonToList(String json) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			return mapper.readValue(json, ArrayList.class);
		} catch (Exception e) {
			return new ArrayList<>();
		}
    }
    
	@Named("toClusterCloudNodeList")
    default ArrayList<ClusterCloudDto.Node> toClusterCloudNodeList(ArrayList<ClusterDto.Node> nodes) {
		try {
			ArrayList<ClusterCloudDto.Node> results = new ArrayList<>();
			
			for (ClusterDto.Node node : nodes) {
				ClusterCloudDto.Node result = new ClusterCloudDto.Node();  
				result.setName(node.getName());
				result.setIp(node.getIp());
				result.setNodeTypes(node.getNodeTypes());
				
				results.add(result); 
			}
			
			return results;
		} catch (Exception e) {
			return new ArrayList<>();
		}
    }
}
