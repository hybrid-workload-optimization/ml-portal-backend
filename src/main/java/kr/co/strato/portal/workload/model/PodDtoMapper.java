package kr.co.strato.portal.workload.model;

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
import kr.co.strato.domain.persistentVolumeClaim.model.PersistentVolumeClaimEntity;
import kr.co.strato.domain.pod.model.PodEntity;
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
    })
    public PodDto.ResListDto toResListDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "podName"),
        @Mapping(target = "namespace", source = "namespace.name"),
        @Mapping(target = "node", source = "node.name"),
        @Mapping(target = "label", source = "label", qualifiedByName = "dataToMap"),
//        @Mapping(target = "cpu", source = "cpu", qualifiedByName = "getCpu"),
//        @Mapping(target = "memory", source = "memory", qualifiedByName = "getMemory"),
    })
    public PodDto.ResListDto toResK8sListDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "podName"),
        @Mapping(target = "namespace", source = "namespace.name"),
        @Mapping(target = "uid", source = "podUid"),
        @Mapping(target = "node", source = "node.name"),
        @Mapping(target = "label", source = "label", qualifiedByName = "dataToMap"),
        @Mapping(target = "annotation", source = "annotation", qualifiedByName = "dataToMap"),
        @Mapping(target = "condition", source = "condition", qualifiedByName = "dataToList"),
    })
    public PodDto.ResDetailDto toResDetailDto(PodEntity entity);
    
    @Mappings({
    	@Mapping(target = "name", source = "entity.statefulSetName"),
    	@Mapping(target = "type", source = "resourceType"),
    	@Mapping(target = "pod", source = "k8s.status", qualifiedByName = "podStatus")
    })
    public PodDto.ResOwnerDto toResStatefulSetOwnerInfoDto(StatefulSetEntity entity, StatefulSet k8s,  String resourceType);

 
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
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(data, HashMap.class);

            return map;
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
    
    @Named("pvcToList")
    default List<HashMap<String, Object>> pvcToList(PersistentVolumeClaimEntity pvcEntity) {
        try{
        	return new ArrayList<>();
//            List<HashMap<String, Object>> list = pvcEntity.stream().map(e => {
//        		return new Map<String,Object> ();
//            }).collect(Collectors.toList());
//            return list;
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
}
