package kr.co.strato.portal.setting.model;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityViewDtoMapper {
	AuthorityViewDtoMapper INSTANCE = Mappers.getMapper(AuthorityViewDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "id", target = "userRoleIdx"),
		@Mapping(source = "parentUserRoleIdx", target = "parentUserRoleIdx"),
		@Mapping(source = "groupYn", target = "groupYn"),
		@Mapping(source = "userDefinedYn", target = "userDefinedYn"),
		@Mapping(source = "userRoleMenus", target = "menuList", qualifiedByName = "toAuthorityViewDtoInnerMenu"),
		@Mapping(source = "users", target = "userList", qualifiedByName = "toAuthorityViewDtoInnerUser")
	})
	@Named("toAuthorityViewDto")
	public AuthorityViewDto toAuthorityViewDto (UserRoleEntity entity);
	
	@Mappings({
		@Mapping(source = "menu.menuIdx", target = "menuIdx"),
		@Mapping(source = "menu.menuName", target = "menuName"),
		@Mapping(source = "menu.menuUrl", target = "menuUrl"),
		@Mapping(source = "menu.parentMenuIdx", target = "parentMenuIdx"),
		@Mapping(source = "menu.menuOrder", target = "menuOrder"),
		@Mapping(source = "menu.menuDepth", target = "menuDepth"),
		@Mapping(source = "menu.useYn", target = "useYn"),
	})
	@Named("toAuthorityViewDtoInnerMenu")
	public AuthorityViewDto.Menu toAuthorityViewDtoInnerMenu (UserRoleMenuEntity entity);
	
	
	@Named("toAuthorityViewDtoInnerUser")
	public AuthorityViewDto.User toAuthorityViewDtoInnerUser (UserEntity entitiy);
	
	@IterableMapping(qualifiedByName = "toAuthorityViewDto")
	List<AuthorityViewDto> toAuthorityViewDtoList (List<UserRoleEntity> entityList);
}
