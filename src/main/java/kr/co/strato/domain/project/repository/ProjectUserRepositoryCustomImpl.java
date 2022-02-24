package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;

import java.util.Date;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

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
						projectUserEntity.id.as("userId"), 
						projectUserEntity.projectIdx,
						userEntity.userName,
						userEntity.email,
						userEntity.organization,
						projectUserEntity.projectUserRole,
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
				  .join(userEntity).on(projectUserEntity.id.eq(userEntity.userId))
				  .where(projectUserEntity.projectIdx.eq(projectIdx))
				  .fetch();
		
		return result;
	}
}
