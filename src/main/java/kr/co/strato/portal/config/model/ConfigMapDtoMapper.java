package kr.co.strato.portal.config.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import kr.co.strato.domain.configMap.model.ConfigMapEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ConfigMapDtoMapper {

	ConfigMapDtoMapper INSTANCE = Mappers.getMapper(ConfigMapDtoMapper.class);
	
	@Mapping(target = "name", 				source = "p.name")
    @Mapping(target = "namespace",			source = "p.namespace.name")
    @Mapping(target = "label",				source = "p.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "age",				source = "p.createdAt")
	public ConfigMapDto.List toList(ConfigMapEntity c);
	
	@Mapping(target = "name", 				source = "p.name")
    @Mapping(target = "namespace",			source = "p.namespace.name")
    @Mapping(target = "label",				source = "p.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "uid",				source = "p.uid")
    @Mapping(target = "createdAt",			source = "p.createdAt")
    public ConfigMapDto.Detail toDetail(ConfigMapEntity c, ConfigMap k8s);
    
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
