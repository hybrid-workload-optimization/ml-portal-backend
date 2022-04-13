package kr.co.strato.domain.user.repository;

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
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.project.model.QProjectUserEntity;
import kr.co.strato.domain.user.model.QUserEntity;
import kr.co.strato.domain.user.model.QUserRoleEntity;
import kr.co.strato.domain.user.model.UserEntity;
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
		
		builder.and(qUserEntity.useYn.eq("Y"));
		

		
		if(param.getProjectId() != null && param.getProjectId() > 0L) {
			builder.and(qProjectUserEntity.projectIdx.eq(param.getProjectId()));
		}
		
		if(param.getAuthorityId()!= null && !"".equals(param.getAuthorityId())) {
			builder.and(qUserRoleEntity.userRoleCode.eq(param.getAuthorityId()));
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

}
