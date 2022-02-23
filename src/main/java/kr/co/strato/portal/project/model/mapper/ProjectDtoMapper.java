package kr.co.strato.portal.project.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.portal.project.model.ProjectDto;

@Mapper
public interface ProjectDtoMapper {

	ProjectDtoMapper INSTANCE = Mappers.getMapper(ProjectDtoMapper.class);

    public ProjectEntity toEntity(ProjectDto dto);

    public ProjectDto toDto(ProjectEntity project);
}
