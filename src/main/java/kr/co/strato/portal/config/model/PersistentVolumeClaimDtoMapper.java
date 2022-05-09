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

import io.fabric8.kubernetes.api.model.PersistentVolumeClaim;
import io.fabric8.kubernetes.api.model.apps.DaemonSet;
import kr.co.strato.domain.daemonset.model.DaemonSetEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.workload.model.DaemonSetDto;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PersistentVolumeClaimDtoMapper {
    PersistentVolumeClaimDtoMapper INSTANCE = Mappers.getMapper(PersistentVolumeClaimDtoMapper.class);

    @Mappings({
        @Mapping(target = "namespace", source = "namespace.name"),
    })
    public PersistentVolumeClaimDto.ResListDto toResListDto(PersistentVolumeClaimEntity entity);
    
    @Mapping(target = "name", 				source = "p.name")
    @Mapping(target = "namespace",			source = "p.namespace.name")
    //@Mapping(target = "label",				source = "p.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "storageCapacity",	source = "p.storageCapacity")
    @Mapping(target = "storageRequest",		source = "p.storageRequest")
    @Mapping(target = "accessType",		    source = "p.accessType")
    @Mapping(target = "storageClass",		source = "p.storageClass")
    @Mapping(target = "age",				source = "p.createdAt")
	public PersistentVolumeClaimDto.List toList(PersistentVolumeClaimEntity p);
    
    @Mapping(target = "name", 				source = "p.name")
    @Mapping(target = "namespace",			source = "p.namespace.name")
    //@Mapping(target = "label",				source = "p.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "status",				source = "k8s.status.phase")
    @Mapping(target = "storageCapacity",	source = "p.storageCapacity")
    @Mapping(target = "storageRequest",		source = "p.storageRequest")
    @Mapping(target = "accessType",		    source = "p.accessType")
    @Mapping(target = "storageClass",		source = "p.storageClass")
    @Mapping(target = "age",				source = "p.createdAt")
	public PersistentVolumeClaimDto.List toList(PersistentVolumeClaimEntity p, PersistentVolumeClaim k8s);
    
    @Mapping(target = "name", 				source = "p.name")
    @Mapping(target = "namespace",			source = "p.namespace.name")
    //@Mapping(target = "label",				source = "p.label",			qualifiedByName = "jsonToMap")
    @Mapping(target = "uid",				source = "p.uid")
    @Mapping(target = "status",				source = "k8s.status.phase")
    //@Mapping(target = "storageCapacity",	source = "p.storageCapacity")
    //@Mapping(target = "storageRequest",		source = "p.storageRequest")
    @Mapping(target = "accessType",		    source = "p.accessType")
    @Mapping(target = "storageClass",		source = "p.storageClass")
    @Mapping(target = "createdAt",			source = "p.createdAt")
    @Mapping(target = "clusterName",			source = "p.namespace.cluster.clusterName")
    @Mapping(target = "clusterIdx",			source = "p.namespace.cluster.clusterIdx")
    public PersistentVolumeClaimDto.Detail toDetail(PersistentVolumeClaimEntity p, PersistentVolumeClaim k8s);
    
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
