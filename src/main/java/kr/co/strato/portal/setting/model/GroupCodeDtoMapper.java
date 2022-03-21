package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.code.model.CodeEntity;
import kr.co.strato.domain.code.model.GroupCodeEntity;
import kr.co.strato.domain.user.model.UserEntity;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface GroupCodeDtoMapper {
	
	GroupCodeDtoMapper INSTANCE = Mappers.getMapper(GroupCodeDtoMapper.class);
		
	GroupCodeEntity toEntity(GroupCodeDto dto);
	
	GroupCodeDto toDto(GroupCodeEntity entity);
}
