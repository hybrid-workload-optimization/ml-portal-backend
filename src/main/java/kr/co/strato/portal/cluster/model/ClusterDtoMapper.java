package kr.co.strato.portal.cluster.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.cluster.model.ClusterEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterDtoMapper {

	ClusterDtoMapper INSTANCE = Mappers.getMapper(ClusterDtoMapper.class);
	
	public ClusterEntity toEntity(ClusterDto dto);
	
	//@Mapping(target = "problem",			source = "cluster.problem",		qualifiedByName = "jsonToMap")
	public ClusterDto toDto(ClusterEntity cluster);
	
	@Named("jsonToMap")
    default HashMap<String, Object> jsonToMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(json, HashMap.class);

            return map;
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
}
