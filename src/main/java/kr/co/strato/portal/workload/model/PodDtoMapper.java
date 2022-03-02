package kr.co.strato.portal.workload.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.pod.model.PodEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PodDtoMapper {
    PodDtoMapper INSTANCE = Mappers.getMapper(PodDtoMapper.class);

    @Mapping(target = "name", source = "podName")
    @Mapping(target = "namespace", source = "namespace.name")
    @Mapping(target = "node", source = "node.name")
    @Mapping(target = "dayAgo", source = "createdAt", qualifiedByName = "getDayAgo")
    @Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
    public PodDto.ResListDto toResListDto(PodEntity entity);

    @Named("getDayAgo")
    default String getDayAgo(LocalDateTime createdAt){
        LocalDateTime now = LocalDateTime.now();
        Period period = Period.between(createdAt.toLocalDate(), now.toLocalDate());

        return String.valueOf(period.getDays());
    }

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
