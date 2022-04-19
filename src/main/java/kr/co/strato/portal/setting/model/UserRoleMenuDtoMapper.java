package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserRoleMenuEntity;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleMenuDtoMapper {
	
	UserRoleMenuDtoMapper INSTANCE = Mappers.getMapper(UserRoleMenuDtoMapper.class);
	
	UserRoleMenuEntity toEntity(UserRoleMenuDto dto);
	UserRoleMenuDto toDto(UserRoleMenuEntity entity);	
	
}
