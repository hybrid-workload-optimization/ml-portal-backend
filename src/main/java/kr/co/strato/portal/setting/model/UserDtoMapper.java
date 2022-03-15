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
public interface UserDtoMapper {
	
	UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "userId",  target = "userId"),
		@Mapping(source = "userName",  target = "userName"),
		@Mapping(source = "organization",  target = "organization"),
		@Mapping(source = "email",  target = "email"),
		@Mapping(source = "contact",  target = "contact"),
		@Mapping(source = "useYn",  target = "useYn"),
		@Mapping(source = "userRole", target = "userRole", qualifiedByName = "toUserDtoInnerUserRole")
	})
	UserEntity toEntity(UserDto dto);
	
	@Mappings({
		@Mapping(source = "userRoleIdx", target = "id"),
		@Mapping(source = "userRoleCode", target = "userRoleCode"),
		@Mapping(source = "userRoleName", target = "userRoleName"),
		@Mapping(source = "description", target = "description"),
		@Mapping(source = "groupYn", target = "groupYn"),
		@Mapping(source = "parentUserRoleIdx", target = "parentUserRoleIdx")
	})
	@Named("toUserDtoInnerUserRole")
	UserRoleEntity toUserDtoInnerUserRole(UserDto.UserRole role);
	
	UserDto toDto(UserEntity entity);
	
}
