package kr.co.strato.portal.workload.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplicaSetDtoMapper {

	ReplicaSetDtoMapper INSTANCE = Mappers.getMapper(ReplicaSetDtoMapper.class);
	
	public ReplicaSetEntity toEntity(ReplicaSetDto replicaSetDto);
	
	public ReplicaSetDto toDto(ReplicaSetEntity replicaSetEntity);
	
	@Mapping(target = "name", source = "replicaSetName")
    @Mapping(target = "namespace", source = "namespace.name")
    @Mapping(target = "age", source = "createdAt")
    @Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	public ReplicaSetDto.List toList(ReplicaSetEntity replicaSetEntity);
	
	@Named("labelToMap")
    default HashMap<String, Object> labelToMap(String label) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(label, HashMap.class);

            return map;
        }catch (JsonProcessingException e){
            return new HashMap<>();
        }
    }
}
