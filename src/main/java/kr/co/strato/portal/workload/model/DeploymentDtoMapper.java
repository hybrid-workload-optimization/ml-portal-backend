package kr.co.strato.portal.workload.model;

import kr.co.strato.domain.deployment.model.DeploymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeploymentDtoMapper {
	DeploymentDtoMapper INSTANCE = Mappers.getMapper(DeploymentDtoMapper.class);

	@Mappings({
		@Mapping(source = "deploymentIdx", target = "idx"), 
		@Mapping(source = "deploymentName", target = "name"),
		@Mapping(source = "deploymentUid", target = "uid"),
		@Mapping(source = "namespaceEntity.id", target = "namespaceIdx"),
		@Mapping(source = "namespaceEntity.name", target = "namespaceName")
	})
	@Named("toDeploymentDto")
	public DeploymentDto toDto(DeploymentEntity entity);

	@Mappings({
		@Mapping(source = "idx", target = "deploymentIdx"),
		@Mapping(source = "name", target = "deploymentName"),
		@Mapping(source = "uid", target = "deploymentUid"),
		@Mapping(source = "namespaceIdx", target = "namespaceEntity.id"),
		@Mapping(source = "namespaceName", target = "namespaceEntity.name")
	})
	@Named("toDeploymentEntity")
	public DeploymentEntity toEntity(DeploymentDto dto);
}
