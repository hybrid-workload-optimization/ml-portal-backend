package kr.co.strato.portal.workload.v1.model;

import kr.co.strato.domain.job.model.JobEntity;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobDtoMapper {
	JobDtoMapper INSTANCE = Mappers.getMapper(JobDtoMapper.class);

	@Mappings({
		@Mapping(source = "jobIdx", target = "idx"), 
		@Mapping(source = "jobName", target = "name"),
		@Mapping(source = "jobUid", target = "uid"),
		@Mapping(source = "namespaceEntity.id", target = "namespaceIdx"),
		@Mapping(source = "namespaceEntity.name", target = "namespace"),
		@Mapping(source = "image", target = "image"),
		@Mapping(source = "label", target = "label", qualifiedByName = "dataToMap"),
		@Mapping(source = "createdAt", target = "createdAt"),
		@Mapping(source = "namespaceEntity.cluster.clusterName", target = "clusterName"),
		@Mapping(source = "namespaceEntity.cluster.clusterIdx", target = "clusterIdx")
	})
	public JobDto toDto(JobEntity entity);

	@Mappings({
		@Mapping(source = "idx", target = "jobIdx"),
		@Mapping(source = "name", target = "jobName"),
		@Mapping(source = "uid", target = "jobUid"),
		@Mapping(source = "namespaceIdx", target = "namespaceEntity.id"),
		@Mapping(source = "namespace", target = "namespaceEntity.name"),
		@Mapping(source = "image", target = "image"),
		@Mapping(source = "label", target = "label" ,ignore = true),
		@Mapping(source = "createdAt", target = "createdAt")
	})
	public JobEntity toEntity(JobDto dto);
	
	@Named("dataToMap")
    default HashMap<String, Object> dataToMap(String data) {
        try{
        	if (data != null) {
	            ObjectMapper mapper = new ObjectMapper();
	            HashMap<String, Object> map = mapper.readValue(data, HashMap.class);
	            return map;
        	} else return new HashMap<>();
        }catch (JsonProcessingException e){
            return new HashMap<>();
        }
    }
}
