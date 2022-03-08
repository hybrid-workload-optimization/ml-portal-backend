package kr.co.strato.portal.networking.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.strato.domain.service.model.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.Period;
import java.time.temporal.TemporalUnit;
import java.util.HashMap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface K8sServiceDtoMapper {
    K8sServiceDtoMapper INSTANCE = Mappers.getMapper(K8sServiceDtoMapper.class);

    @Mapping(target = "name", source = "serviceName")
    @Mapping(target = "namespace", source = "namespace.name")
    @Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
    @Mapping(target = "type", source = "serviceType")
    @Mapping(target = "age", source = "createdAt")
    public K8sServiceDto.ResListDto toResListDto(ServiceEntity entity);

    @Named("labelToMap")
    default HashMap<String, Object> labelToMap(String label){
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(label, HashMap.class);

            return map;
        }catch (JsonProcessingException e){
            return new HashMap<>();
        }
    }


}
