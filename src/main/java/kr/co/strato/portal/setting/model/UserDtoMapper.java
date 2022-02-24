package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserEntity;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface UserDtoMapper {
	
	UserDtoMapper INSTANCE = Mappers.getMapper(UserDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "userId",  target = "userId"),
		@Mapping(source = "userName",  target = "userName"),
		@Mapping(source = "organization",  target = "organization"),
		@Mapping(source = "email",  target = "email"),
		@Mapping(source = "contact",  target = "contact"),
		@Mapping(source = "useYn",  target = "useYn")
	})
	UserEntity toEntity(UserDto dto);
	
	UserDto toDto(UserEntity entity);
}
