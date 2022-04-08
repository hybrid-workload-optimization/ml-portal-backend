package kr.co.strato.portal.networking.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.co.strato.domain.service.model.ServiceEndpointEntity;
import kr.co.strato.domain.service.model.ServiceEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;
import java.util.HashMap;
import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface K8sServiceDtoMapper {
    K8sServiceDtoMapper INSTANCE = Mappers.getMapper(K8sServiceDtoMapper.class);

    @Mapping(target = "name", source = "serviceName")
    @Mapping(target = "namespace", source = "namespace.name")
    @Mapping(target = "label", source = "label", qualifiedByName = "stringToMap")
    @Mapping(target = "internalEndpoints", source = "internalEndpoint", qualifiedByName = "stringToMapList")
    @Mapping(target = "externalEndpoints", source = "externalEndpoint", qualifiedByName = "stringToMapList")
    @Mapping(target = "age", source = "createdAt")
    public K8sServiceDto.ResListDto toResListDto(ServiceEntity entity);

    @Mapping(target = "label", source = "service.label", qualifiedByName = "stringToMap")
    @Mapping(target = "annotation", source = "service.annotation", qualifiedByName = "stringToMap")
    @Mapping(target = "selector", source = "service.selector", qualifiedByName = "stringToMap")
    @Mapping(target = "externalEndpoints", source = "service.externalEndpoint", qualifiedByName = "stringToMapList")
    @Mapping(target = "internalEndpoints", source = "service.internalEndpoint", qualifiedByName = "stringToMapList")
    @Mapping(target = "namespaceName", source = "service.namespace.name")
    @Mapping(target = "endpoints", source = "endpoints")
    @Mapping(target = "clusterId", source = "clusterId")
    @Mapping(target = "clusterName", source = "clusterName")
    public K8sServiceDto.ResDetailDto toDetailDto(ServiceEntity service, List<ServiceEndpointEntity> endpoints, Long clusterId, String clusterName);

    @Named("stringToMap")
    default HashMap<String, Object> stringToMap(String text){
        if(text == null){
            return null;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(text, HashMap.class);
            return map;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }

    @Named("stringToMapList")
    default List<HashMap<String, Object>> stringToMapList(String text){
        if(text == null){
            return null;
        }
        try{
            ObjectMapper mapper = new ObjectMapper();
            List<HashMap<String, Object>> maps = mapper.readValue(text, new TypeReference<List<HashMap<String, Object>>>() {
            });
            return maps;
        }catch (JsonProcessingException e){
            e.printStackTrace();
            return null;
        }
    }
}
