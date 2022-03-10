package kr.co.strato.portal.cluster.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.namespace.model.NamespaceEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterNamespaceDtoMapper {

	ClusterNamespaceDtoMapper INSTANCE = Mappers.getMapper(ClusterNamespaceDtoMapper.class);

	@Mapping(target = "clusterIdx" , source = "clusterIdx.clusterIdx")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterNamespaceDto.ResListDto toResListDto(NamespaceEntity node);
	
	
	@Mapping(target = "clusterIdx" , source = "clusterIdx.clusterIdx")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterNamespaceDto.ResDetailDto toResDetailDto(NamespaceEntity node);
	
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
}
