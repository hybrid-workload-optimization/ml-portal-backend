package kr.co.strato.portal.cluster.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.storageClass.model.StorageClassEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ClusterStorageClassDtoMapper {

	ClusterStorageClassDtoMapper INSTANCE = Mappers.getMapper(ClusterStorageClassDtoMapper.class);
	
	@Mapping(target = "clusterIdx" , source = "clusterIdx.clusterIdx")
	public ClusterStorageClassDto toDto(StorageClassEntity storageClass);
}
