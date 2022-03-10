package kr.co.strato.portal.workload.model;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.pod.model.PodEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PodDtoMapper {
    PodDtoMapper INSTANCE = Mappers.getMapper(PodDtoMapper.class);

    @Mappings({
    	@Mapping(target = "name", source = "podName"),
        @Mapping(target = "namespace", source = "namespace.name"),
        @Mapping(target = "node", source = "node.name"),
        @Mapping(target = "dayAgo", source = "createdAt", qualifiedByName = "getDayAgo"),
        @Mapping(target = "label", source = "label", qualifiedByName = "dataToMap"),
        @Mapping(target = "cpu", source = "cpu", qualifiedByName = "getCpu"),
        @Mapping(target = "memory", source = "memory", qualifiedByName = "getMemory"),
    })
    public PodDto.ResListDto toResListDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "entity.podName"),
        @Mapping(target = "namespace", source = "entity.namespace.name"),
        @Mapping(target = "node", source = "entity.node.name"),
        @Mapping(target = "label", source = "entity.label", qualifiedByName = "dataToMap"),
        @Mapping(target = "condition", source = "entity.condition", qualifiedByName = "dataToList"),
    })
    public PodDto.ResDetailDto toResDetailDto(PodEntity entity);
    // TODO Detail 정보에 k8s 정보 추가
    
    @Named("getCpu")
    default float getCpu(float cpu){
        return Math.round(cpu * 100) / 100;
    }
    
    @Named("getMemory")
    default float getMemory(float memory){
        return Math.round(memory * 100) / 100;
    }

    @Named("getDayAgo")
    default String getDayAgo(LocalDateTime createdAt){
        LocalDateTime now = LocalDateTime.now();
        Period period = Period.between(createdAt.toLocalDate(), now.toLocalDate());

        return String.valueOf(period.getDays());
    }

    @Named("dataToMap")
    default HashMap<String, Object> dataToMap(String data) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(data, HashMap.class);

            return map;
        }catch (JsonProcessingException e){
            return new HashMap<>();
        }
    }
    
    @Named("dataToList")
    default List<HashMap<String, Object>> dataToList(String data) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            List<HashMap<String, Object>> map = mapper.readValue(data, List.class);

            return map;
        }catch (JsonProcessingException e){
            return new ArrayList<>();
        }
    }
    
}
