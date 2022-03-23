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
            
            List<ContainerStatus> containerList = status.getContainerStatuses();
            
            List<OwnerReference> ownerList = metadata.getOwnerReferences(); 
            
            
            String ownerKind = null;
            String ownerUid = null;
            Integer restart = 0;
            ContainerStatus containerStatus = null;
            if (!containerList.isEmpty()) {
            	containerStatus = containerList.get(0);
            	restart = containerStatus.getRestartCount();
            }
            if (!ownerList.isEmpty()) {
            	OwnerReference ownerReference = ownerList.get(0);
            	
            	String kind = ownerReference.getKind();
                ownerKind = (kind != null) ? kind.substring(0, 1).toLowerCase() + kind.substring(1) : null;
                ownerUid = ownerReference.getUid();
            }

            //k8s Object -> Entity
            String name = metadata.getName();
            String namespace = metadata.getNamespace();
            String nodeName = spec.getNodeName();
            String uid = metadata.getUid();
            String ip = status.getPodIP();
            String statusStr = status.getPhase();
            String qosClass = status.getQosClass();
            
            
            List<Quantity> cpuRequests = spec.getContainers().stream()
    				.map(container -> container.getResources().getRequests())
    				.filter(map -> map != null)
    				.map(map -> map.get("cpu"))
    				.filter(quantity -> quantity != null)
    				.map(map -> {
    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
    					return map;
    				})
    				.collect(Collectors.toList());
    		List<Quantity> cpuLimits = spec.getContainers().stream()
    				.map(container -> container.getResources().getLimits())
    				.filter(map -> map != null)
    				.map(map -> map.get("cpu"))
    				.filter(quantity -> quantity != null)
    				.map(map -> {
    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
    					return map;
    				})
    				.collect(Collectors.toList());
    		
    		List<Quantity> memRequests = spec.getContainers().stream()
    				.map(container -> container.getResources().getRequests())
    				.filter(map -> map != null)
    				.map(map -> map.get("memory"))
    				.filter(quantity -> quantity != null)
    				.map(map -> {
    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
    					return map;
    				})
    				.collect(Collectors.toList());	
    		List<Quantity> memLimits = spec.getContainers().stream()
    				.map(container -> container.getResources().getLimits())
    				.filter(map -> map != null)
    				.map(map -> map.get("memory"))
    				.filter(quantity -> quantity != null)
    				.map(map -> {
    					map.setAmount(map.getAmount().replaceAll("[^0-9]", ""));
    					map.setFormat(map.getFormat().replaceAll("[^a-zA-Z]", ""));
    					return map;
    				})
    				.collect(Collectors.toList());

            // TODO CPU + Memory 계산 작업 필요 (Mi, Gi 외의 값...)
    		// 기준은 GB 단위
            Float cpu = 0F;
            for (Quantity cpuLimit : cpuLimits) {
            	Float amount = Float.parseFloat(cpuLimit.getAmount());
            	String format = cpuLimit.getFormat().replaceAll("[^a-zA-Z]", "");
            	if (format.equals("m")) {
            		cpu = (float) ((cpu + amount) / 1000.0);
            	} else {
            		cpu = (float) (cpu + amount);
            	}
            }
            Float memory = 0F;
            for (Quantity memoryLimit : memLimits) {
            	Float amount = Float.parseFloat(memoryLimit.getAmount().replaceAll("[^0-9]", ""));
            	String format = memoryLimit.getFormat().replaceAll("[^a-zA-Z]", "");
            	Float coef = 1073741824F;
        		if (format.equals("Mi")) {
            		memory = (float) ((memory) + (amount * 1000000.0 / coef));
            	} else if (format.equals("Gi")) {
            		memory = (float) (memory + (amount * 1000000000.0 / coef));
            	} else if (format.equals("M")) {
            		memory = (float) ((memory + amount) / 1000.0);
            	} else if (format.equals("G")) {
            		memory = (float) (memory + amount);
            	}
            }
            
            
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
                    .cpuLimits(cpuLimits)
                    .cpuRequests(cpuRequests)
                    .memoryLimits(memLimits)
                    .memoryRequests(memRequests)
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
