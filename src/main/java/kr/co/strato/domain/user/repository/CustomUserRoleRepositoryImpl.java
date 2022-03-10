package kr.co.strato.domain.user.repository;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.global.util.OrderUtil;
import kr.co.strato.portal.setting.model.AuthorityRequestDto;

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
	public Page<AuthorityRequestDto> getListPagingUserRoleToDto(AuthorityRequestDto.ReqViewDto params, Pageable pageable) {
		BooleanBuilder builder = new BooleanBuilder();
		if ( ObjectUtils.isNotEmpty(params) ) {
			if ( params.getUserRoleIdx() != null && params.getUserRoleIdx() > 0 ){
				builder.and(condition(params.getUserRoleIdx(), userRoleEntity.id::eq));
			}
			
			if ( StringUtils.isNotEmpty(params.getUserRoleName()) ) {
				builder.and(condition(params.getUserRoleName(), userRoleEntity.userRoleName::eq));
			}
		}
		
		QueryResults<AuthorityRequestDto> results = jpaQueryFactory
				.select(Projections.fields(AuthorityRequestDto.class, 
								userRoleEntity.id.as("userRoleIdx")
							,	userRoleEntity.userRoleName
						))
				.from(userRoleEntity)
				.where(builder)
				.orderBy(OrderUtil.getOrderSpecifier(userRoleEntity, pageable.getSort()))
				.offset(pageable.getOffset())
				.limit(pageable.getPageSize())
				.fetchResults();
		
		List<AuthorityRequestDto> content = results.getResults();
		long total = results.getTotal();
		
		return new PageImpl<>(content, pageable, total);
	}
}
