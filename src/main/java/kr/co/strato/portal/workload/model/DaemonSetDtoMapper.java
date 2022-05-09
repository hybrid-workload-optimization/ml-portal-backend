package kr.co.strato.portal.workload.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import io.fabric8.kubernetes.api.model.apps.ReplicaSet;
import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DaemonSetDtoMapper {

	DaemonSetDtoMapper INSTANCE = Mappers.getMapper(DaemonSetDtoMapper.class);
	
	public DaemonSetEntity toEntity(DaemonSetDto daemonSetDto);
	
	public DaemonSetDto toDto(DaemonSetEntity daemonSetEntity);
	
	@Mapping(target = "name", 				source = "d.daemonSetName")
    @Mapping(target = "namespace",			source = "d.namespace.name")
    @Mapping(target = "age",				source = "d.createdAt")
    @Mapping(target = "label",				source = "d.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "clusterName",		source = "d.namespace.cluster.clusterName")
	public DaemonSetDto.List toList(DaemonSetEntity d);
	
	@Mapping(target = "name", 				source = "d.daemonSetName")
    @Mapping(target = "namespace",			source = "d.namespace.name")
    @Mapping(target = "age",				source = "d.createdAt")
    @Mapping(target = "label",				source = "d.label",			qualifiedByName = "jsonToMap")
	@Mapping(target = "runningPod",			source = "k8s.status.numberReady")
    @Mapping(target = "desiredPod",			source = "k8s.status.desiredNumberScheduled")
    @Mapping(target = "clusterName",		source = "d.namespace.cluster.clusterName")
	public DaemonSetDto.List toList(DaemonSetEntity d, DaemonSet k8s);
	
	@Mapping(target = "daemonSetIdx",		source = "d.daemonSetIdx")
    @Mapping(target = "name",				source = "d.daemonSetName")
    @Mapping(target = "namespace",			source = "d.namespace.name")
    @Mapping(target = "uid",				source = "d.daemonSetUid")
    @Mapping(target = "label",				source = "d.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "annotation",			source = "d.annotation",	qualifiedByName = "jsonToMap")
	@Mapping(target = "createdAt",			source = "d.createdAt")
	@Mapping(target = "selector",			source = "d.selector",		qualifiedByName = "jsonToMap")
	@Mapping(target = "image",				source = "d.image")
	@Mapping(target = "runningPod",			source = "k8s.status.numberReady")
    @Mapping(target = "desiredPod",			source = "k8s.status.desiredNumberScheduled")
	@Mapping(target = "clusterName",		source = "d.namespace.cluster.clusterName")
	@Mapping(target = "clusterIdx",			source = "d.namespace.cluster.clusterIdx")
    public DaemonSetDto.Detail toDetail(DaemonSetEntity d, DaemonSet k8s);
	
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
