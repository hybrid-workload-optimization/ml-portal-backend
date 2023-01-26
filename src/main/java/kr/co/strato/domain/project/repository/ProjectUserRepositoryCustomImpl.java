package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;
import static kr.co.strato.domain.user.model.QUserRoleEntity.userRoleEntity;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProjectUserRepositoryCustomImpl implements ProjectUserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<ProjectUserDto> getProjectUserListExceptManager(Long projectIdx) {
		
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
								JPAExpressions.select(userRoleEntity.userRoleName)
				                              .from(userRoleEntity)
				                              .where(userRoleEntity.id.eq(projectUserEntity.userRoleIdx)),
				          		"userRoleName"
				        ),
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
				  .where(projectUserEntity.projectIdx.eq(projectIdx).and(userEntity.useYn.eq("Y")).and(projectUserEntity.userRoleIdx.ne(JPAExpressions.select(userRoleEntity.id).from(userRoleEntity).where(userRoleEntity.userRoleCode.eq(ProjectUserEntity.PROJECT_MANAGER)))))
				  .orderBy(projectUserEntity.userRoleIdx.asc(), userEntity.userName.asc())
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
				.orderBy(userEntity.userRole.parentUserRoleIdx.asc(), userEntity.userName.asc())
				.fetch();
		
		return result;
	}
	
	@Override
	public List<UserEntity> getAvailableProjectUserList(UserDto loginUser) {
		
		BooleanBuilder builder = new BooleanBuilder();
		
		builder
			.and(userEntity.userRole.userRoleCode.ne(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN))
			.and(userEntity.userRole.userRoleCode.ne(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN));
		
		if(loginUser != null && !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN) 
	    		&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
			//시스템 어드민 , 포탈 어드민이 아닌 일반 사용자인 경우
			//본인이 생성한 사용자만 보이도록 수정.
			builder.and(userEntity.createUserId.eq(loginUser.getUserId()));
		}
		
		
		
		List<UserEntity> result = queryFactory.select(userEntity).from(userEntity)
				.where(builder)
				.orderBy(userEntity.userRole.parentUserRoleIdx.asc(), userEntity.userName.asc())
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
				.orderBy(userEntity.userRole.parentUserRoleIdx.asc(), userEntity.userName.asc())
				.fetch();
		
		return result;
	}

	@Override
	public UserRoleEntity getProjectUserRole(Long projectIdx, String userId) {
		UserRoleEntity result = queryFactory
				.select(userRoleEntity)
				.from(projectUserEntity)
				.join(userRoleEntity).on(projectUserEntity.userRoleIdx.eq(userRoleEntity.id))
				.where(projectUserEntity.projectIdx.eq(projectIdx).and(projectUserEntity.userId.eq(userId)))
				.fetchOne();
		return result;
	}
	
	@Override
	public ProjectUserEntity getProjectManagerInfo(Long projectIdx) {
		
		ProjectUserEntity result = queryFactory
				.select(projectUserEntity).from(projectUserEntity)
				.where(projectUserEntity.projectIdx.eq(projectIdx).and(projectUserEntity.userRoleIdx.eq(JPAExpressions.select(userRoleEntity.id).from(userRoleEntity).where(userRoleEntity.userRoleCode.eq(ProjectUserEntity.PROJECT_MANAGER)))))
				.fetchOne();
		
		return result;
	}
	
	@Override
	public List<UserEntity> getUserWithManagerList() {
		
		List<UserEntity> result = queryFactory
				.select(userEntity).from(userEntity)
				.where(userEntity.useYn.eq("Y").and(userEntity.userRole.userRoleCode.eq(ProjectUserEntity.PROJECT_MANAGER)))
				.fetch();
		
		return result;
	}
	
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
								JPAExpressions.select(userRoleEntity.userRoleName)
				                              .from(userRoleEntity)
				                              .where(userRoleEntity.id.eq(projectUserEntity.userRoleIdx)),
				          		"userRoleName"
				        ),
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
				  .orderBy(projectUserEntity.userRoleIdx.asc(), userEntity.userName.asc())
				  .fetch();
		
		return result;
	}
	
	@Override
	public UserEntity getProjectManager(Long projectIdx) {
		
		UserEntity result = queryFactory
				.select(userEntity)
				.from(projectUserEntity)
				.join(userEntity).on(projectUserEntity.userId.eq(userEntity.userId))
				.where(projectUserEntity.projectIdx.eq(projectIdx).and(projectUserEntity.userRoleIdx.eq(ProjectUserEntity.PROJECT_MANAGER_ROLE_IDX)))
				.fetchOne();
		
		return result;
	}
}
