package kr.co.strato.portal.cluster.model;

import java.util.ArrayList;
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
	@Mapping(target = "clusterName", source = "cluster.clusterName")
	@Mapping(target = "role", source = "role", qualifiedByName = "roleToList")
	public ClusterNodeDto.ResListDto toResListDto(NodeEntity node);
	
	@Mapping(target = "clusterIdx", source = "cluster.clusterIdx")
	@Mapping(target = "clusterName", source = "cluster.clusterName")
	@Mapping(target = "role", source = "role", qualifiedByName = "roleToList")
	public ClusterNodeDto.ResListDetailDto toResListDetailDto(NodeEntity node);

	@Mapping(target = "clusterIdx", source = "cluster.clusterIdx")
	@Mapping(target = "clusterId", source = "cluster.clusterId")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	@Mapping(target = "condition", source = "condition", qualifiedByName = "dataToList")
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
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				e.printStackTrace();
			}

			return map;
	}

	  @Named("dataToList")
	    default List<HashMap<String, Object>> dataToList(String data) {
	        try{
	        	ObjectMapper mapper = new ObjectMapper();
	            List<HashMap<String, Object>> list = mapper.readValue(data, List.class);
	            return list;
	        }catch (Exception e){
	            return new ArrayList<>();
	        }
	    }
}
