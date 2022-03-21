package kr.co.strato.portal.workload.model;

import kr.co.strato.domain.job.model.JobEntity;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobDtoMapper {
	JobDtoMapper INSTANCE = Mappers.getMapper(JobDtoMapper.class);

	@Mappings({
		@Mapping(source = "jobIdx", target = "idx"), 
		@Mapping(source = "jobName", target = "name"),
		@Mapping(source = "jobUid", target = "uid"),
		@Mapping(source = "namespaceEntity.id", target = "namespaceIdx"),
		@Mapping(source = "namespaceEntity.name", target = "namespaceName")
	})
	@Named("toDto")
	public JobDto toDto(JobEntity entity);

	@Mappings({
		@Mapping(source = "idx", target = "jobIdx"),
		@Mapping(source = "name", target = "jobName"),
		@Mapping(source = "uid", target = "jobUid"),
		@Mapping(source = "namespaceIdx", target = "namespaceEntity.id"),
		@Mapping(source = "namespaceName", target = "namespaceEntity.name")
	})
	@Named("toEntity")
	public JobEntity toEntity(JobDto dto);
}
