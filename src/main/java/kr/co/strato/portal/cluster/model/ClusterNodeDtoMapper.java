package kr.co.strato.portal.cluster.model;

import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.node.model.NodeEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterNodeDtoMapper {

	ClusterNodeDtoMapper INSTANCE = Mappers.getMapper(ClusterNodeDtoMapper.class);

	@Mapping(target = "clusterIdx", source = "cluster.clusterIdx")
	@Mapping(target = "role", source = "role", qualifiedByName = "roleToList")
	public ClusterNodeDto.ResListDto toResListDto(NodeEntity node);

	@Mapping(target = "clusterIdx", source = "cluster.clusterIdx")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	@Mapping(target = "condition", source = "condition", qualifiedByName = "labelToMap")
	@Mapping(target = "role", source = "role", qualifiedByName = "roleToList")
	public ClusterNodeDto.ResDetailDto toResDetailDto(NodeEntity node);

	@Named("labelToMap")
	default HashMap<String, Object> labelToMap(String label) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> map = mapper.readValue(label, HashMap.class);

			return map;
		} catch (JsonProcessingException e) {
			return new HashMap<>();
		}
	}
	
	@Named("roleToList")
	default List<String> roleToList(String role) {
			ObjectMapper mapper = new ObjectMapper();
			List<String> map = null;
			try {
				map = mapper.readValue(role, List.class);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return map;
	}

}
