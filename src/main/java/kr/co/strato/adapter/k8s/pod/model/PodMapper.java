package kr.co.strato.adapter.k8s.pod.model;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Pod;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;


@Mapper
public interface PodMapper {
    PodMapper INSTANCE = Mappers.getMapper(PodMapper.class);

    default PodEntity toEntity(Pod pod){
        try {
            ObjectMapper mapper = new ObjectMapper();

            //k8s Object -> Entity
            String name = pod.getMetadata().getName();
            String namespace = pod.getMetadata().getNamespace();
            String uid = pod.getMetadata().getUid();
            String ip = pod.getStatus().getPodIP();
//            Integer restart = pod.getMetadata().getRestart();
//            Integer cpu = pod.getMetadata().getCpu();
//            Integer memory = pod.getMetadata().getMemory();
//            String ownerUid = pod.getMetadata().getOwnerUid();
            String label = mapper.writeValueAsString(pod.getMetadata().getLabels());
            String annotations = mapper.writeValueAsString(pod.getMetadata().getAnnotations());
            String createAt = pod.getMetadata().getCreationTimestamp();

            PodEntity podEntity = PodEntity.builder()
                    .podName(name)
                    .podUid(uid)
                    .label(ip)
                    .label(label)
                    .annotation(annotations)
                    .createdAt(DateUtil.strToLocalDateTime(createAt))
                    .build();


            return podEntity;
        } catch (Exception e) {
            throw new InternalServerException("Failed to convert k8s statefulSet model to statefulSet entity");
        }
    }
}
