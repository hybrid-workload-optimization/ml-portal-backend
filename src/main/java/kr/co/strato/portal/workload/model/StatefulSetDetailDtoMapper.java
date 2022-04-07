package kr.co.strato.portal.workload.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.global.util.DateUtil;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.HashMap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatefulSetDetailDtoMapper {
    StatefulSetDetailDtoMapper INSTANCE = Mappers.getMapper(StatefulSetDetailDtoMapper.class);

    @Mapping(target = "id", source = "entity.id")
    @Mapping(target = "name", source = "entity.statefulSetName")
    @Mapping(target = "namespace", source = "entity.namespace.name")
    @Mapping(target = "uid", source = "entity.statefulSetUid")
    @Mapping(target = "createdAt", source = "entity.createdAt", qualifiedByName = "creatdAtToString")
    @Mapping(target = "label", source = "entity.label", qualifiedByName = "jsonStringToMap")
    @Mapping(target = "annotation", source = "entity.annotation", qualifiedByName = "jsonStringToMap")
    @Mapping(target = "replicas", source = "k8s.status.readyReplicas")
    @Mapping(target = "readyReplicas", source = "k8s.status.replicas")
    @Mapping(target = "clusterId", source = "clusterId")
    @Mapping(target = "projectName", source = "projectName")
    @Mapping(target = "clusterName", source = "clusterName")
    public StatefulSetDetailDto.ResDetailDto toResDetailDto(StatefulSetEntity entity, StatefulSet k8s, Long clusterId, String projectName, String clusterName);

    @Named("creatdAtToString")
    default String creatdAtToString(LocalDateTime createdAt){
        return DateUtil.localDateTimeToStr(createdAt, "yyyy-MM-dd HH:mm:ss");
    }

    @Named("jsonStringToMap")
    default HashMap<String, Object> jsonStringToMap(String jsonString) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(jsonString, HashMap.class);

            return map;
        }catch (JsonProcessingException e){
            return new HashMap<>();
        }
    }


}
