package kr.co.strato.portal.cluster.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.fabric8.kubernetes.api.model.Namespace;
import kr.co.strato.domain.namespace.model.NamespaceEntity;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterNamespaceDtoMapper {

	ClusterNamespaceDtoMapper INSTANCE = Mappers.getMapper(ClusterNamespaceDtoMapper.class);

	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterNamespaceDto.ResListDto toResListDto(NamespaceEntity node);
	
	
	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	@Mapping(target = "clusterId" , source = "cluster.clusterId")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterNamespaceDto.ResDetailDto toResDetailDto(NamespaceEntity node);
	
	@Named("labelToMap")
	default HashMap<String, Object> labelToMap(String label) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			HashMap<String, Object> map = mapper.readValue(label, HashMap.class);

			return map;
		} catch (JsonProcessingException e) {
			return new HashMap<>();
		}
	}
	
	default Object toDto(Namespace n, boolean isDefault) {
		try {
			ObjectMapper mapper = new ObjectMapper();
			String name = n.getMetadata().getName();
			String uid = n.getMetadata().getUid();
			String status = n.getStatus().getPhase();
			String createdAt = n.getMetadata().getCreationTimestamp();
			String annotations = mapper.writeValueAsString(n.getMetadata().getAnnotations());
			String label = mapper.writeValueAsString(n.getMetadata().getLabels());

			if(isDefault) {
				ClusterNamespaceDto.ResDetailDto list = ClusterNamespaceDto.ResDetailDto.builder()
						.name(name)
						.uid(uid)
						.label(labelToMap(label))
						.status(status)
						.annotation(labelToMap(annotations))
						.createdAt(DateUtil.strToLocalDateTime(createdAt))
						.build();
				return list;
				
			} else {
				ClusterNamespaceDto.ResListDto list = ClusterNamespaceDto.ResListDto.builder()
						.name(name)
						.uid(uid)
						.label(labelToMap(label))
						.status(status)
						.annotation(labelToMap(annotations))
						.createdAt(DateUtil.strToLocalDateTime(createdAt))
						.build();
				return list;
			}			
		} catch (Exception e) {
            throw new InternalServerException("Failed to convert k8s Namespace model to Dto");
        }
	}
}
