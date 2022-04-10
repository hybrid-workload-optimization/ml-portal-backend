package kr.co.strato.portal.workload.model;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.time.Period;
import java.util.HashMap;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface StatefulSetDtoMapper {
    StatefulSetDtoMapper INSTANCE = Mappers.getMapper(StatefulSetDtoMapper.class);

    @Mapping(target = "name", source = "statefulSetName")
    @Mapping(target = "namespace", source = "namespace.name")
    @Mapping(target = "age", source = "createdAt")
    @Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
    @Mapping(target = "clusterName", source = "entity.namespace.cluster.clusterName")
    public StatefulSetDto.ResListDto toResListDto(StatefulSetEntity entity);

    @Mapping(target = "name", source = "entity.statefulSetName")
    @Mapping(target = "namespace", source = "entity.namespace.name")
    @Mapping(target = "age", source = "entity.createdAt")
    @Mapping(target = "label", source = "entity.label", qualifiedByName = "labelToMap")
    @Mapping(target = "clusterName", source = "entity.namespace.cluster.clusterName")
    @Mapping(target = "replicas", source = "k8s.status.replicas")
    @Mapping(target = "readyReplicas", source = "k8s.status.readyReplicas")
    public StatefulSetDto.ResListDto toResListDto(StatefulSetEntity entity, StatefulSet k8s);

    @Named("getDayAgo")
    default String getDayAgo(LocalDateTime createdAt){
        LocalDateTime now = LocalDateTime.now();
        Period period = Period.between(createdAt.toLocalDate(), now.toLocalDate());

        return String.valueOf(period.getDays());
    }

    @Named("labelToMap")
    default HashMap<String, Object> labelToMap(String label) {
        try{
            ObjectMapper mapper = new ObjectMapper();
            HashMap<String, Object> map = mapper.readValue(label, HashMap.class);

            return map;
        }catch (JsonProcessingException e){
            return new HashMap<>();
        }
    }
}
