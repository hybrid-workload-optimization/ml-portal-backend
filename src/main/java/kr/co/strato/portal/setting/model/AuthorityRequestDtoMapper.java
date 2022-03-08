package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityRequestDtoMapper {
	AuthorityRequestDtoMapper INSTANCE = Mappers.getMapper(AuthorityRequestDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "userRoleIdx", target = "id")
	})
	public UserRoleEntity toEntity (AuthorityRequestDto.ReqRegistDto dto);
	
	public UserEntity toUserEntity (AuthorityRequestDto.User dto);
}
