package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserRoleDtoMapper {
	
	UserRoleDtoMapper INSTANCE = Mappers.getMapper(UserRoleDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "userRoleIdx", target = "id"),
		@Mapping(source = "parentUserRoleIdx", target = "parentUserRoleIdx"),
		@Mapping(source = "groupYn", target = "groupYn"),
		@Mapping(source = "userRoleName", target = "userRoleName"),
		@Mapping(source = "userRoleCode", target = "userRoleCode"),
		@Mapping(source = "description", target = "description"),
	})
	UserRoleEntity toEntity(UserRoleDto dto);
	
	@Mappings({
		@Mapping(source = "id", target = "userRoleIdx"),
		@Mapping(source = "parentUserRoleIdx", target = "parentUserRoleIdx"),
		@Mapping(source = "groupYn", target = "groupYn"),
		@Mapping(source = "userRoleName", target = "userRoleName"),
		@Mapping(source = "userRoleCode", target = "userRoleCode"),
		@Mapping(source = "description", target = "description"),
	})
	UserRoleDto toDto(UserRoleEntity entity);
	
}
