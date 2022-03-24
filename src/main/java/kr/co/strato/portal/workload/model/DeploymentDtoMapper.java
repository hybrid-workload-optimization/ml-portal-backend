package kr.co.strato.portal.workload.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.DeploymentCondition;
import io.fabric8.kubernetes.api.model.apps.DeploymentStatus;
import kr.co.strato.domain.deployment.model.DeploymentEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.util.HashMap;
import java.util.List;

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
			@Mapping(source = "entity.deploymentIdx", target = "idx"),
			@Mapping(source = "entity.deploymentName", target = "name"),
			@Mapping(source = "entity.deploymentUid", target = "uid"),
			@Mapping(source = "entity.namespaceEntity.id", target = "namespaceIdx"),
			@Mapping(source = "entity.namespaceEntity.name", target = "namespaceName"),
			@Mapping(source = "status.replicas", target = "podReplicas"),
			@Mapping(source = "status.readyReplicas", target = "podReady")
	})
	public DeploymentDto toDto(DeploymentEntity entity, DeploymentStatus status);

	@Mappings({
			@Mapping(source = "entity.deploymentIdx", target = "idx"),
			@Mapping(source = "entity.deploymentName", target = "name"),
			@Mapping(source = "entity.deploymentUid", target = "uid"),
			@Mapping(source = "entity.namespaceEntity.id", target = "namespaceIdx"),
			@Mapping(source = "entity.namespaceEntity.name", target = "namespaceName"),
			@Mapping(source = "clusterId", target = "clusterId"),
			@Mapping(source = "replicaSetUid", target = "replicaSetUid")
	})
	public DeploymentDto toDto(DeploymentEntity entity, Long clusterId, String replicaSetUid);

	@Mappings({
			@Mapping(source = "entity.deploymentIdx", target = "idx"),
			@Mapping(source = "entity.deploymentName", target = "name"),
			@Mapping(source = "entity.deploymentUid", target = "uid"),
			@Mapping(source = "entity.namespaceEntity.id", target = "namespaceIdx"),
			@Mapping(source = "entity.namespaceEntity.name", target = "namespaceName"),
			@Mapping(source = "clusterId", target = "clusterId"),
			@Mapping(source = "replicaSetUid", target = "replicaSetUid"),
			@Mapping(source = "status.conditions", target = "condition", qualifiedByName = "deploymentConditionToString"),
			@Mapping(source = "status.updatedReplicas", target = "podUpdated"),
			@Mapping(source = "status.replicas", target = "podReplicas"),
			@Mapping(source = "status.readyReplicas", target = "podReady")
	})
	public DeploymentDto toDto(DeploymentEntity entity, Long clusterId, String replicaSetUid, DeploymentStatus status);

	@Mappings({
		@Mapping(source = "idx", target = "deploymentIdx"),
		@Mapping(source = "name", target = "deploymentName"),
		@Mapping(source = "uid", target = "deploymentUid"),
		@Mapping(source = "namespaceIdx", target = "namespaceEntity.id"),
		@Mapping(source = "namespaceName", target = "namespaceEntity.name")
	})
	@Named("toDeploymentEntity")
	public DeploymentEntity toEntity(DeploymentDto dto);

	@Named("deploymentConditionToString")
	default String deploymentConditionToString(List<DeploymentCondition> conditions){

		try{
			ObjectMapper mapper = new ObjectMapper();
			String result = mapper.writeValueAsString(conditions);
			return result;
		}catch (JsonProcessingException e){
			return null;
		}
	}
}
