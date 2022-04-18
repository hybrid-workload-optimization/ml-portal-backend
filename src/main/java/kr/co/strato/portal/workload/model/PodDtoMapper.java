package kr.co.strato.portal.workload.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import io.fabric8.kubernetes.api.model.apps.StatefulSetStatus;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.job.model.JobEntity;
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.replicaset.model.ReplicaSetEntity;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface PodDtoMapper {
    PodDtoMapper INSTANCE = Mappers.getMapper(PodDtoMapper.class);

    @Mappings({
    	@Mapping(target = "name", source = "podName"),
        @Mapping(target = "namespace", source = "namespace.name"),
        @Mapping(target = "node", source = "node.name"),
        @Mapping(target = "label", source = "label", qualifiedByName = "dataToMap"),
        @Mapping(target = "cpu", source = "cpu", qualifiedByName = "getCpu"),
        @Mapping(target = "memory", source = "memory", qualifiedByName = "getMemory"),
        @Mapping(target = "clusterName", source = "namespace.cluster.clusterName"),
    })
    public PodDto.ResListDto toResListDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "podName"),
        @Mapping(target = "namespace", source = "namespace.name"),
        @Mapping(target = "node", source = "node.name"),
        @Mapping(target = "label", source = "label", qualifiedByName = "dataToMap"),
        @Mapping(target = "cpu", source = "cpu", qualifiedByName = "getCpu"),
        @Mapping(target = "memory", source = "memory", qualifiedByName = "getMemory"),
    })
    public PodDto.ResListDto toResK8sListDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "podName"),
        @Mapping(target = "namespace", source = "namespace.name"),
        @Mapping(target = "clusterId", source = "namespace.cluster.clusterId"),
        @Mapping(target = "uid", source = "podUid"),
        @Mapping(target = "node", source = "node.name"),
        @Mapping(target = "label", source = "label", qualifiedByName = "dataToMap"),
        @Mapping(target = "annotation", source = "annotation", qualifiedByName = "dataToMap"),
        @Mapping(target = "condition", source = "condition", qualifiedByName = "dataToList"),
        @Mapping(target = "images", source = "image", qualifiedByName = "stringToList"),
        @Mapping(target = "clusterName", source = "namespace.cluster.clusterName"),
    })
    public PodDto.ResDetailDto toResDetailDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "entity.statefulSetName"),
    	@Mapping(target = "type", source = "resourceType"),
    	@Mapping(target = "pod", source = "k8s.status", qualifiedByName = "podStatus")
    })
    public PodDto.ResOwnerDto toResStatefulSetOwnerInfoDto(StatefulSetEntity entity, StatefulSet k8s,  String resourceType);
    
//    @Mappings({
//    	@Mapping(target = "name", source = "entity.statefulSetName"),
//    	@Mapping(target = "type", source = "resourceType"),
//    	@Mapping(target = "pod", source = "k8s.status", qualifiedByName = "podStatus")
//    })
//    public PodDto.ResOwnerDto toResDaemonSetOwnerInfoDto(DaemonSetEntity entity, StatefulSet k8s,  String resourceType);
    
    @Mappings({
    	@Mapping(target = "name", source = "entity.replicaSetName"),
    	@Mapping(target = "type", source = "resourceType"),
    	@Mapping(target = "pod", source = "k8s.status", qualifiedByName = "podStatus"),
    	@Mapping(target = "createdAt", source = "entity.createdAt", qualifiedByName = "createdAt")
    })
    public PodDto.ResOwnerDto toResReplicaSetOwnerInfoDto(ReplicaSetEntity entity, StatefulSet k8s,  String resourceType);
    
    @Mappings({
    	@Mapping(target = "name", source = "entity.jobName"),
    	@Mapping(target = "type", source = "resourceType"),
    	@Mapping(target = "pod", source = "k8s.status", qualifiedByName = "podStatus")
    })
    public PodDto.ResOwnerDto toResJobOwnerInfoDto(JobEntity entity, StatefulSet k8s,  String resourceType);

 
    @Named("getCpu")
    default float getCpu(float cpu){
        return (float) (Math.round(cpu * 100) / 100.0);
    }
    
    @Named("getMemory")
    default float getMemory(float memory){
        return (float) (Math.round(memory * 100) / 100.0);
    }

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
    
    @Named("dataToList")
    default List<HashMap<String, Object>> dataToList(String data) {
        try{
        	ObjectMapper mapper = new ObjectMapper();
            List<HashMap<String, Object>> list = mapper.readValue(data, List.class);
            return list;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    
    @Named("stringToList")
    default List<String> stringToList(String data) {
    	try{
        	ObjectMapper mapper = new ObjectMapper();
            List<String> list = mapper.readValue(data, List.class);
            return list;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    
    @Named("podStatus")
    default String podStatus(StatefulSetStatus status) {
    	try{
    		int readyReplicas = status.getReadyReplicas();
    		int replicas = status.getReplicas();
        	return readyReplicas + "/" + replicas;
        }catch (Exception e){
            return null;
        }
    }
    
    @Named("createAt")
    default LocalDateTime createAt(String createAt) {
    	return LocalDateTime.parse(createAt, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.S"));
    }
    
}
