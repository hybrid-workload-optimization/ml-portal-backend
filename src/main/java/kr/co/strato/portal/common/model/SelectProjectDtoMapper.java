package kr.co.strato.portal.common.model;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.portal.workload.model.DeploymentDtoMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SelectProjectDtoMapper {
    SelectProjectDtoMapper INSTANCE = Mappers.getMapper(SelectProjectDtoMapper.class);

    @Mapping(target = "name", source = "projectName")
    public SelectProjectDto toDto(ProjectEntity entity);
}
