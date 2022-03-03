package kr.co.strato.portal.setting.model;

import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserRoleEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityDtoMapper {
	AuthorityDtoMapper INSTANCE = Mappers.getMapper(AuthorityDtoMapper.class);
	
	public UserRoleEntity toEntity(AuthorityDto dto);
}
