package kr.co.strato.portal.workload.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ReplicaSetDtoMapper {

	ReplicaSetDtoMapper INSTANCE = Mappers.getMapper(ReplicaSetDtoMapper.class);
	
	public ReplicaSetEntity toEntity(ReplicaSetDto replicaSetDto);
	
	public ReplicaSetDto toDto(ReplicaSetEntity replicaSetEntity);
	
	@Mapping(target = "name", 				source = "r.replicaSetName")
    @Mapping(target = "namespace",			source = "r.namespace.name")
    @Mapping(target = "age",				source = "r.createdAt")
    @Mapping(target = "label",				source = "r.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "clusterName",				source = "r.namespace.cluster.clusterName")
	public ReplicaSetDto.List toList(ReplicaSetEntity r);
	
	@Mapping(target = "name", 				source = "r.replicaSetName")
    @Mapping(target = "namespace",			source = "r.namespace.name")
    @Mapping(target = "age",				source = "r.createdAt")
    @Mapping(target = "label",				source = "r.label",			qualifiedByName = "jsonToMap")
	@Mapping(target = "runningPod",			source = "k8s.status.readyReplicas")
    @Mapping(target = "desiredPod",			source = "k8s.status.replicas")
    @Mapping(target = "clusterName",				source = "r.namespace.cluster.clusterName")
	public ReplicaSetDto.List toList(ReplicaSetEntity r, ReplicaSet k8s);
	
	@Mapping(target = "replicaSetIdx",		source = "r.replicaSetIdx")
    @Mapping(target = "name",				source = "r.replicaSetName")
    @Mapping(target = "namespace",			source = "r.namespace.name")
    @Mapping(target = "uid",				source = "r.replicaSetUid")
    @Mapping(target = "label",				source = "r.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "annotation",			source = "r.annotation",	qualifiedByName = "jsonToMap")
	@Mapping(target = "createdAt",			source = "r.createdAt")
	@Mapping(target = "selector",			source = "r.selector",		qualifiedByName = "jsonToMap")
	@Mapping(target = "image",				source = "r.image")
	@Mapping(target = "runningPod",			source = "k8s.status.readyReplicas")
    @Mapping(target = "desiredPod",			source = "k8s.status.replicas")
	@Mapping(target = "clusterName",		source = "r.namespace.cluster.clusterName")
	@Mapping(target = "clusterIdx",		source = "r.namespace.cluster.clusterIdx")
	@Mapping(target = "clusterId",			source = "r.namespace.cluster.clusterId")
    public ReplicaSetDto.Detail toDetail(ReplicaSetEntity r, ReplicaSet k8s);
	
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
