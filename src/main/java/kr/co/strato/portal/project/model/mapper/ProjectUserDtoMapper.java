package kr.co.strato.portal.project.model.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;

@Mapper(componentModel="spring", unmappedTargetPolicy=ReportingPolicy.IGNORE)
public interface ProjectUserDtoMapper {

	ProjectUserDtoMapper INSTANCE = Mappers.getMapper(ProjectUserDtoMapper.class);

    public ProjectUserEntity toEntity(ProjectUserDto dto);

    public ProjectUserDto toDto(ProjectUserEntity projectUser);
}
