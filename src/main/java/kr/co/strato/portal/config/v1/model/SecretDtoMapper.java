package kr.co.strato.portal.config.v1.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.secret.model.SecretEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SecretDtoMapper {

	SecretDtoMapper INSTANCE = Mappers.getMapper(SecretDtoMapper.class);
	
	@Mapping(target = "name", 				source = "s.name")
    @Mapping(target = "namespace",			source = "s.namespace.name")
    @Mapping(target = "age",				source = "s.createdAt")
	public SecretDto.List toList(SecretEntity s);
	
	@Mapping(target = "name", 				source = "s.name")
    @Mapping(target = "namespace",			source = "s.namespace.name")
    @Mapping(target = "uid",				source = "s.uid")
	@Mapping(target = "data",				source = "s.data")
    @Mapping(target = "createdAt",			source = "s.createdAt")
	@Mapping(target = "clusterName",		source = "s.namespace.cluster.clusterName")
    public SecretDto.Detail toDetail(SecretEntity s);
	
	/*@Named("jsonToMap")
    default HashMap<String, Object> jsonToMap(String json) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(json, HashMap.class);

            return map;
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }*/
}
