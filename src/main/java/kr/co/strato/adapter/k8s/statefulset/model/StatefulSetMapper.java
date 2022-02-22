package kr.co.strato.adapter.k8s.statefulset.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.fabric8.kubernetes.api.model.apps.StatefulSet;
import kr.co.strato.domain.statefulset.model.StatefulSetEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.cluster.model.ClusterDtoMapper;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;


@Mapper
public interface StatefulSetMapper {
    StatefulSetMapper INSTANCE = Mappers.getMapper(StatefulSetMapper.class);

    default StatefulSetEntity toEntity(StatefulSet statefulSet){
        try {
            ObjectMapper mapper = new ObjectMapper();

            //k8s Object -> Entity
            String name = statefulSet.getMetadata().getName();
            String namespace = statefulSet.getMetadata().getNamespace();
            String uid = statefulSet.getMetadata().getUid();
            String image = statefulSet.getSpec().getTemplate().getSpec().getContainers().get(0).getImage();
            String label = mapper.writeValueAsString(statefulSet.getMetadata().getLabels());
            String annotations = mapper.writeValueAsString(statefulSet.getMetadata().getAnnotations());
            String createAt = statefulSet.getMetadata().getCreationTimestamp();

            StatefulSetEntity statefulSetEntity = StatefulSetEntity.builder()
                    .statefulSetName(name)
                    .statefulSetUid(uid)
                    .image(image)
                    .label(label)
                    .annotation(annotations)
                    .createdAt(DateUtil.strToLocalDateTime(createAt))
                    .build();


            return statefulSetEntity;
        } catch (Exception e) {
            throw new InternalServerException("Failed to convert k8s statefulSet model to statefulSet entity");
        }
    }
}
