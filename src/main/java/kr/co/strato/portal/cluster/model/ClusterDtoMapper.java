package kr.co.strato.portal.cluster.model;

import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.node.model.NodeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterDtoMapper {

	ClusterDtoMapper INSTANCE = Mappers.getMapper(ClusterDtoMapper.class);
	
	public ClusterEntity toEntity(ClusterDto dto);
	
	public ClusterDto toDto(ClusterEntity cluster);
	
	@Mapping(target = "nodeCount", 	source = "c.nodes",			qualifiedByName = "nodeCount")
    @Mapping(target = "problem",	source = "c.problem",		qualifiedByName = "jsonToMap")
	public ClusterDto.List toList(ClusterEntity c);
	
    @Mapping(target = "problem",	source = "c.problem",		qualifiedByName = "jsonToMap")
	public ClusterDto.Detail toDetail(ClusterEntity c);
	
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
}
