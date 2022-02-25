package kr.co.strato.portal.common.model;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SelectClusterDtoMapper {
    SelectClusterDtoMapper INSTANCE = Mappers.getMapper(SelectClusterDtoMapper.class);

    @Mapping(target = "id", source = "entity.clusterIdx")
    @Mapping(target = "text", source = "entity.clusterName")
    @Mapping(target = "value", source = "entity.clusterIdx")
    public SelectClusterDto toDto(ClusterEntity entity, Long projectId);
}
