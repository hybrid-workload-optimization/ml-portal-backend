package kr.co.strato.portal.project.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.portal.project.model.ProjectClusterDto;

@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface ProjectClusterDtoMapper {

	ProjectClusterDtoMapper INSTANCE = Mappers.getMapper(ProjectClusterDtoMapper.class);

    public ProjectClusterEntity toEntity(ProjectClusterDto dto);

    public ProjectClusterDto toDto(ProjectClusterEntity projectCluster);
}
