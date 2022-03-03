package kr.co.strato.portal.setting.model;

import java.util.List;

import org.mapstruct.IterableMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import org.mapstruct.factory.Mappers;

import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.domain.user.model.UserRoleMenuEntity;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface AuthorityViewDtoMapper {
	AuthorityViewDtoMapper INSTANCE = Mappers.getMapper(AuthorityViewDtoMapper.class);
	
	@Mappings({
		@Mapping(source = "id", target = "userRoleIdx"),
		@Mapping(source = "userRoleMenus", target = "menuList", qualifiedByName = "toMenuDto")
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
	@Named("toMenuDto")
	public MenuDto toMenuDto (UserRoleMenuEntity entity);
	
	@IterableMapping(qualifiedByName = "toAuthorityViewDto")
	List<AuthorityViewDto> toAuthorityViewDtoList (List<UserRoleEntity> entityList);
}
