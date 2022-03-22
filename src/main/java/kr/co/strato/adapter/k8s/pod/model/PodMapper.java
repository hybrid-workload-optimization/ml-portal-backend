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
            String qosClass = status.getQosClass();
            
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

            // TODO CPU + Memory 계산 작업 필요 (Mi, Gi 외의 값...)
            Long cpu = 0L;
            for (Quantity cpuLimit : cpuLimits) {
            	Long amount = Long.parseLong(cpuLimit.getAmount().replaceAll("[^0-9]", ""));
            	String format = cpuLimit.getFormat().replaceAll("[^a-zA-Z]", "");
            	if (format.equals("m")) {
            		cpu = (long) ((cpu + amount) / 100.0);
            	} else if (format.equals("G")) {
            		cpu = (long) (cpu + amount);
            	}
            }
            Long memory = 0L;
            for (Quantity memoryLimit : memLimits) {
            	Long amount = Long.parseLong(memoryLimit.getAmount().replaceAll("[^0-9]", ""));
            	String format = memoryLimit.getFormat().replaceAll("[^a-zA-Z]", "");
        		if (format.equals("Mi")) {
            		memory = (long) ((memory + amount) / 1024.0);
            	} else if (format.equals("Gi")) {
            		memory = (long) memory + amount;
            	}
            }
            String kind = ownerReference.getKind();
            String ownerKind = (kind != null) ? kind.substring(0, 1).toLowerCase() + kind.substring(1) : null;
    		
            String ownerUid = ownerReference.getUid();
            
            String label = mapper.writeValueAsString(metadata.getLabels());
            String annotations = mapper.writeValueAsString(metadata.getAnnotations());
            String createAt = metadata.getCreationTimestamp();
            String conditions = mapper.writeValueAsString(status.getConditions());

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
                    .qosClass(qosClass)
                    .cpu(cpu)
                    .memory(memory)
                    .kind(ownerKind)
                    .ownerUid(ownerUid)
                    .annotation(annotations)
                    .condition(conditions)
                    .createdAt(DateUtil.strToLocalDateTime(createAt))
                    .build();


            return podEntity;
        } catch (Exception e) {
            throw new InternalServerException("Failed to convert k8s statefulSet model to statefulSet entity");
        }
    }
}
