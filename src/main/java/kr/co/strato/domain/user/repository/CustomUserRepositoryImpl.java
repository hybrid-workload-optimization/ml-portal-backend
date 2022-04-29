package kr.co.strato.domain.user.repository;

import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;
import static kr.co.strato.domain.user.model.QUserRoleEntity.userRoleEntity;
import static kr.co.strato.domain.user.model.QUserRoleMenuEntity.userRoleMenuEntity;
import static kr.co.strato.domain.menu.model.QMenuEntity.menuEntity;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.project.model.QProjectUserEntity;
import kr.co.strato.domain.user.model.QUserEntity;
import kr.co.strato.domain.user.model.QUserRoleEntity;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.setting.model.UserDto;
import kr.co.strato.portal.setting.model.UserDto.SearchParam;

public class CustomUserRepositoryImpl implements CustomUserRepository {
	
	private final JPAQueryFactory jpaQueryFactory;

	public CustomUserRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
		this.jpaQueryFactory = jpaQueryFactory;
	}

	private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
		return Optional.ofNullable(value).map(function).orElse(null);
	}
	
	@Override
	public Page<UserEntity> getListUserWithParam(Pageable pageable, SearchParam param) {
		
		QUserEntity	qUserEntity = QUserEntity.userEntity;
		QProjectUserEntity qProjectUserEntity = QProjectUserEntity.projectUserEntity;
		QUserRoleEntity qUserRoleEntity = QUserRoleEntity.userRoleEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		
		builder.and(qUserEntity.useYn.eq("Y")).and(qUserEntity.userRole.id.ne(1L));
		

		
		if(param.getProjectId() != null && param.getProjectId() > 0L) {
			builder.and(qProjectUserEntity.projectIdx.eq(param.getProjectId()));
		}
		
		if(param.getAuthorityId()!= null && !"".equals(param.getAuthorityId())) {
			builder.and(qUserRoleEntity.userRoleCode.eq(param.getAuthorityId()));
		}
		
		if(param.getNotAuthorityId()!= null && !"".equals(param.getNotAuthorityId())) {
			//해당 권한이 아닌 사용자만 리턴.
			builder.and(qUserRoleEntity.userRoleCode.ne(param.getNotAuthorityId()));
		}
		
		QueryResults<UserEntity> results = null;
		
		if(param.getProjectId() == null || param.getProjectId() == 0) {
			results = jpaQueryFactory
					.select(qUserEntity)
					.from(qUserEntity)
					.where(builder)
					.join(qUserEntity.userRole, qUserRoleEntity)
					.where(builder)
					.offset(pageable.getOffset())
					.groupBy(qUserEntity.userId)
					.orderBy(qUserEntity.createdAt.desc())
					.fetchResults();
			
		}else {
			results = jpaQueryFactory
					.select(qUserEntity)
					.from(qUserEntity)
					.where(builder)
					.join(qUserEntity.projectUser, qProjectUserEntity)
					.join(qUserEntity.userRole, qUserRoleEntity)
					.where(builder)
					.offset(pageable.getOffset())
					.groupBy(qUserEntity.userId)
					.orderBy(qUserEntity.createdAt.desc())
					.fetchResults();	
		}
		
		
	
		List<UserEntity> list = results.getResults();
		long total = results.getTotal();
		
		return new PageImpl<>(list, pageable, total);

	}
	
	@Override
	public List<UserDto.UserMenuDto> getUserMenu(String userId) {
		
		List<UserDto.UserMenuDto> result = jpaQueryFactory
				.select(Projections.fields(
						UserDto.UserMenuDto.class,
						userEntity.userId, 
						menuEntity.menuName,
						userRoleMenuEntity.viewableYn,
						userRoleMenuEntity.writableYn
				  ))
				  .from(userEntity)
				  .join(userRoleEntity).on(userEntity.userRole.id.eq(userRoleEntity.id))
				  .join(userRoleMenuEntity).on(userRoleEntity.id.eq(userRoleMenuEntity.userRole.id))
				  .join(menuEntity).on(userRoleMenuEntity.menu.menuIdx.eq(menuEntity.menuIdx))
				  .where(userEntity.userId.eq(userId))
				  .fetch();
		
		return result;
	}

}
