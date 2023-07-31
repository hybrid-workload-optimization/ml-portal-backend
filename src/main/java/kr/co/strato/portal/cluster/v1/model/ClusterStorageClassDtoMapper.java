package kr.co.strato.portal.cluster.v1.model;

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
public interface ClusterStorageClassDtoMapper {

	ClusterStorageClassDtoMapper INSTANCE = Mappers.getMapper(ClusterStorageClassDtoMapper.class);
	
	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterStorageClassDto.ResListDto toResListDto(StorageClassEntity storageClass);

	@Mapping(target = "clusterIdx" , source = "cluster.clusterIdx")
	@Mapping(target = "clusterId" , source = "cluster.clusterId")
	@Mapping(target = "label", source = "label", qualifiedByName = "labelToMap")
	@Mapping(target = "annotation", source = "annotation", qualifiedByName = "labelToMap")
	public ClusterStorageClassDto.ResDetailDto toResDetailDto(StorageClassEntity storageClass);

	public ClusterStorageClassDto.PvList toPvListDto(PersistentVolumeEntity persistentVolumeEntity);

	
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
