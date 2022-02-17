package kr.co.strato.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.portal.setting.model.UserRoleDto;

import static kr.co.strato.domain.user.model.QUserRoleEntity.userRoleEntity;
import static kr.co.strato.domain.user.model.QUserRoleMenuEntity.userRoleMenuEntity;

public class CustomUserRoleRepositoryImpl implements CustomUserRoleRepository {
	private final JPAQueryFactory jpaQueryFactory;

	public CustomUserRoleRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}
	
	private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
		return Optional.ofNullable(value).map(function).orElse(null);
	}
	
	
	@Override
	public List<UserRoleDto> getListUserRoleToDto(UserRoleDto params) {
		BooleanBuilder builder = new BooleanBuilder();
		if ( ObjectUtils.isNotEmpty(params) ) {
			if ( params.getUserRoleIdx() > 0 ){
				builder.and(condition(params.getUserRoleIdx(), userRoleEntity.userRoleIdx::eq));
			}
			
			if ( StringUtils.isNotEmpty(params.getUserRoleName()) ) {
				builder.and(condition(params.getUserRoleName(), userRoleEntity.userRoleName::eq));
			}
		}
		
		return jpaQueryFactory
				.select(Projections.fields(UserRoleDto.class, 
								userRoleEntity.userRoleIdx
							,	userRoleEntity.userRoleName
						))
				.from(userRoleEntity)
				.where(builder)
				.fetch();
	}
}
