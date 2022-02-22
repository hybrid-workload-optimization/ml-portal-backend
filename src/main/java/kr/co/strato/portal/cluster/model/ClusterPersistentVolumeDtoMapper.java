package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.persistentVolume.model.PersistentVolumeEntity;
import kr.co.strato.domain.storageClass.model.StorageClassEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterPersistentVolumeDtoMapper {

	ClusterPersistentVolumeDtoMapper INSTANCE = Mappers.getMapper(ClusterPersistentVolumeDtoMapper.class);
	
	@Mapping(target = "clusterIdx" , source = "clusterIdx.clusterIdx")
	@Mapping(target = "storageClassIdx" , source = "storageClassIdx.id")
	public ClusterPersistentVolumeDto toDto(PersistentVolumeEntity persistentVolume);
	
	
	
}
