package kr.co.strato.portal.cluster.model;

import java.util.HashMap;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterPersistentVolumeDtoMapper {

	ClusterPersistentVolumeDtoMapper INSTANCE = Mappers.getMapper(ClusterPersistentVolumeDtoMapper.class);
	
	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	@Mapping(target = "storageClassIdx" , source = "storageClass.id")
	@Mapping(target = "storageClassName" , source = "storageClass.name")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterPersistentVolumeDto.ResListDto toResListDto(PersistentVolumeEntity persistentVolume);
	
	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	@Mapping(target = "storageClassIdx" , source = "storageClass.id")
	@Mapping(target = "storageClassName" , source = "storageClass.name")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterPersistentVolumeDto.ResDetailDto toResDetailDto(PersistentVolumeEntity persistentVolume);

	
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
	
}
