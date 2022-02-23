package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.project.model.QProjectClusterEntity.projectClusterEntity;
import static kr.co.strato.domain.project.model.QProjectEntity.projectEntity;
import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.portal.project.model.ProjectDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProjectRepositoryCustomImpl implements ProjectRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	/*public CustomerProjectRepositoryImpl(EntityManager entityManager) {
		this.jpaQueryFactory = new JPAQueryFactory(entityManager);
	}*/
	
	@Override
	public List<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) {
		
		BooleanBuilder builder = new BooleanBuilder();
	    if(!"".equals(param.getProjectName()) && param.getProjectName() != null) {
	    	builder.and(projectEntity.projectName.contains(param.getProjectName()));
	    }
		
		//QueryResults<ProjectDto> result = queryFactory
		List<ProjectDto> result = queryFactory
				  .select(Projections.fields(
						  ProjectDto.class,
						  projectEntity.id, 
						  projectEntity.projectName,
						  projectEntity.description, 
						  ExpressionUtils.as(
								  JPAExpressions.select(projectClusterEntity.clusterIdx.count())
	                                            .from(projectClusterEntity)
	                                            .where(projectClusterEntity.projectIdx.eq(projectEntity.id)),
	                              "clusterCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.id.count())
	                                            .from(projectUserEntity)
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id)),
	                              "userCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.userName)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.id))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectUserName"),
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})",projectEntity.updatedAt, "%Y-%m-%d"),
								  "updatedAt")
				  ))
				  .from(projectEntity)
				  .where(projectEntity.id.in(
						 JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.id.eq(param.getUserId()), builder))
				  )
				  .fetch();
		
		return result;
	}
	
	@Override
	public ProjectDto getProjectDetail(Long projectIdx) {
		
		BooleanBuilder builder = new BooleanBuilder();
	    if(projectIdx != null) {
	    	builder.and(projectEntity.id.eq(projectIdx));
	    }
		
		ProjectDto result = queryFactory
				  .select(Projections.fields(
						  ProjectDto.class,
						  projectEntity.id, 
						  projectEntity.projectName,
						  projectEntity.description, 
						  ExpressionUtils.as(
								  JPAExpressions.select(projectClusterEntity.clusterIdx.count())
	                                            .from(projectClusterEntity)
	                                            .where(projectClusterEntity.projectIdx.eq(projectEntity.id)),
	                              "clusterCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.id.count())
	                                            .from(projectUserEntity)
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id)),
	                              "userCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.userName)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.id))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectUserName"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.email)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.id))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectUserEmail"),
						  
						  projectEntity.updatedAt
				  ))
				  .from(projectEntity)
				  .where(projectEntity.id.eq(projectIdx)
				  )
				  .fetchOne();
		
		return result;
	}
}
