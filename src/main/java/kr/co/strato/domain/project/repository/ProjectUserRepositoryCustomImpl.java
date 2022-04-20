package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProjectUserRepositoryCustomImpl implements ProjectUserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<ProjectUserDto> getProjectUserList(Long projectIdx) {
		
		List<ProjectUserDto> result = queryFactory
				.select(Projections.fields(
						ProjectUserDto.class,
						projectUserEntity.userId.as("userId"), 
						projectUserEntity.projectIdx,
						userEntity.userName,
						userEntity.email,
						userEntity.organization,
						//projectUserEntity.projectUserRole,
						projectUserEntity.userRoleIdx,
						ExpressionUtils.as(
								Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectUserEntity.createdAt, "%Y-%m-%d %H:%i"),
								"createdAt"
						),
						ExpressionUtils.as(
								Expressions.numberTemplate(Integer.class, "DATEDIFF(DATE_FORMAT(NOW(), '%Y-%m-%d'), DATE_FORMAT({0}, '%Y-%m-%d'))", projectUserEntity.createdAt),
								"addDayCount"
						)
				  ))
				  .from(projectUserEntity)
				  .join(userEntity).on(projectUserEntity.userId.eq(userEntity.userId))
				  .where(projectUserEntity.projectIdx.eq(projectIdx).and(userEntity.useYn.eq("Y")))
				  .orderBy(projectUserEntity.userRoleIdx.asc())
				  .fetch();
		
		return result;
	}
	
	@Override
	public List<UserEntity> getProjectUserListExceptUse(Long projectIdx) {
		
		List<UserEntity> result = queryFactory.select(userEntity).from(userEntity)
				.where(userEntity.userId
						.notIn(JPAExpressions.select(projectUserEntity.userId).from(projectUserEntity)
								.where(projectUserEntity.projectIdx.eq(projectIdx)))
						.and(userEntity.useYn.eq("Y"))
						.and(userEntity.userRole.userRoleCode.ne(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN))
						.and(userEntity.userRole.userRoleCode.ne(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN))
				)
				.fetch();
		
		return result;
	}
	
	@Override
	public List<UserEntity> getAvailableProjectUserList() {
		
		List<UserEntity> result = queryFactory.select(userEntity).from(userEntity)
				.where(userEntity.useYn.eq("Y")
						.and(userEntity.userRole.userRoleCode.ne(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN))
						.and(userEntity.userRole.userRoleCode.ne(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN))
				)
				.fetch();
		
		return result;
	}
}
