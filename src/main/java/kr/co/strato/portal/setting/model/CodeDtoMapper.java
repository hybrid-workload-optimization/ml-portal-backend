package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.code.model.CodeEntity;
import kr.co.strato.domain.user.model.UserEntity;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CodeDtoMapper {
	
	CodeDtoMapper INSTANCE = Mappers.getMapper(CodeDtoMapper.class);
		
	CodeEntity toEntity(CodeDto dto);
	
	CodeDto toDto(CodeEntity entity);
}
