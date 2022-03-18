package kr.co.strato.adapter.k8s.pod.model;

import java.util.List;
import java.util.stream.Collectors;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.ContainerStatus;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.OwnerReference;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodSpec;
import io.fabric8.kubernetes.api.model.PodStatus;
import io.fabric8.kubernetes.api.model.Quantity;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.domain.node.model.NodeEntity;
import kr.co.strato.domain.pod.model.PodEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;


@Mapper
public interface PodMapper {
    PodMapper INSTANCE = Mappers.getMapper(PodMapper.class);

    default PodEntity toEntity(Pod pod){
        try {
            ObjectMapper mapper = new ObjectMapper();
            
            ObjectMeta metadata = pod.getMetadata();
            PodSpec spec = pod.getSpec();
            PodStatus status = pod.getStatus();
            ContainerStatus containerStatus = status.getContainerStatuses().get(0);
            OwnerReference ownerReference = metadata.getOwnerReferences().get(0);

            //k8s Object -> Entity
            String name = metadata.getName();
            String namespace = metadata.getNamespace();
            String nodeName = spec.getNodeName();
            String uid = metadata.getUid();
            String ip = status.getPodIP();
            String statusStr = status.getPhase();
            
            Integer restart = containerStatus.getRestartCount();
            
//            Integer cpuRequests = spec.getContainers().stream()
//    				.map(container -> container.getResources().getRequests())
//    				.filter(map -> map != null)
//    				.map(map -> map.get("cpu"))
//    				.filter(quantity -> quantity != null)				
//    				.collect(Collectors.toList());
    		List<Quantity> cpuLimits = spec.getContainers().stream()
    				.map(container -> container.getResources().getLimits())
    				.filter(map -> map != null)
    				.map(map -> map.get("cpu"))
    				.filter(quantity -> quantity != null)				
    				.collect(Collectors.toList());
    		
//    		List<Quantity> memRequests = spec.getContainers().stream()
//    				.map(container -> container.getResources().getRequests())
//    				.filter(map -> map != null)
//    				.map(map -> map.get("memory"))
//    				.filter(quantity -> quantity != null)				
//    				.collect(Collectors.toList());	
    		List<Quantity> memLimits = spec.getContainers().stream()
    				.map(container -> container.getResources().getLimits())
    				.filter(map -> map != null)
    				.map(map -> map.get("memory"))
    				.filter(quantity -> quantity != null)				
    				.collect(Collectors.toList());

            // TODO CPU + Memory 계산 작업 필요 (Mi, Gi 등...)
//            Long cpu = cpuLimits.stream().mapToLong(mapper::intValue).sum();
//            Long memory = 0;
            String ownerUid = ownerReference.getUid();
            String ownerKind = ownerReference.getKind();
            String label = mapper.writeValueAsString(metadata.getLabels());
            String annotations = mapper.writeValueAsString(metadata.getAnnotations());
            String createAt = metadata.getCreationTimestamp();

            NamespaceEntity namespaceEntity = NamespaceEntity.builder().name(namespace).build();
            NodeEntity nodeEntity = NodeEntity.builder().name(nodeName).build();
            PodEntity podEntity = PodEntity.builder()
            		.namespace(namespaceEntity)
            		.node(nodeEntity)
                    .podName(name)
                    .podUid(uid)
                    .ip(ip)
                    .label(label)
                    .restart(restart)
                    .status(statusStr)
//                    .cpu(cpu)
//                    .memory(memory)
                    .kind(ownerKind)
                    .ownerUid(ownerUid)
                    .annotation(annotations)
                    .createdAt(DateUtil.strToLocalDateTime(createAt))
                    .build();


            return podEntity;
        } catch (Exception e) {
            throw new InternalServerException("Failed to convert k8s statefulSet model to statefulSet entity");
        }
    }
}
