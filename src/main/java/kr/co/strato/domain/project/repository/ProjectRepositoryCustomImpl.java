package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.project.model.QProjectClusterEntity.projectClusterEntity;
import static kr.co.strato.domain.project.model.QProjectEntity.projectEntity;
import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;

import java.util.Date;
import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.setting.model.UserDto;
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
	public PageImpl<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) throws Exception {
		
		BooleanBuilder builder = new BooleanBuilder();
	    if(!"".equals(param.getProjectName()) && param.getProjectName() != null) {
	    	builder.and(projectEntity.projectName.contains(param.getProjectName()));
	    }
		
		QueryResults<ProjectDto> result = queryFactory
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
								  JPAExpressions.select(projectUserEntity.userId.count())
	                                            .from(projectUserEntity)
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id)),
	                              "userCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.userId)
	                                            .from(projectUserEntity)
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectPmId"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.userName)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.userId))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectPmName"),
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.createdAt, "%Y-%m-%d"),
								  "createdAt"),
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.updatedAt, "%Y-%m-%d %H:%i:%s"),
								  "updatedAt")
				  ))
				  .from(projectEntity)
				  .where(projectEntity.id.in(
						 JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.userId.eq(param.getUserId()), builder)), projectEntity.deletedYn.eq("N")
				  )
				  .offset(pageable.getOffset())
                  .limit(pageable.getPageSize())
				  .fetchResults();
		
		List<ProjectDto> resultList = result.getResults();
		if(resultList != null && resultList.size() > 0) {
			for(ProjectDto dto : resultList) {
				if(param.getUserId().equals(dto.getProjectPmId())) {
					dto.setOwner(dto.getProjectPmId());
				}
				
				Date today = DateUtil.toDate(DateUtil.currentDateTime("yyyy-MM-dd"), "yyyy-MM-dd");
				Date create = DateUtil.toDate(dto.getCreatedAt(), "yyyy-MM-dd");
				long diffSec = (today.getTime() - create.getTime()) / 1000;
				long compare = diffSec / (24 * 60 * 60);
				if(compare <= 3) {
					dto.setFresh(dto.getCreatedAt());
				}
			}
		}
        long total = result.getTotal();
		
		return new PageImpl<ProjectDto>(resultList, pageable, total);
	}
	
	@Override
	public ProjectDto getProjectDetail(Long projectIdx, String type) {
		
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
								  JPAExpressions.select(projectUserEntity.userId.count())
	                                            .from(projectUserEntity)
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id)),
	                              "userCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.userId)
	                                            .from(projectUserEntity)
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectPmId"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.userName)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.userId))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectPmName"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.email)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.userId))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                              "projectPmEmail"),
						  
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.createdAt, "%Y-%m-%d %H:%i"),
								  "createdAt"
								)
				  ))
				  .from(projectEntity)
				  .where(projectEntity.id.eq(projectIdx)
				  )
				  .fetchOne();
		
		/*ProjectDto projectInfo = result;
		if(!"".equals(projectInfo.getDescription()) && projectInfo.getDescription() != null) {
			String description = "";
			if("view".equals(type)) {
				description = projectInfo.getDescription().replaceAll("\n", "<br />");
			} else {
				description = projectInfo.getDescription().replaceAll("<br />", "\n");
			}
					
			projectInfo.setDescription(description);
		}*/
		
		return result;
	}
	
	public ProjectEntity getProjectDetailByClusterId(Long clusterIdx) {
		
		ProjectEntity result = queryFactory
				.select(projectEntity)
				 .from(projectEntity)
				 .join(projectClusterEntity).on(projectEntity.id.eq(projectClusterEntity.projectIdx))
				 .where(projectClusterEntity.clusterIdx.eq(clusterIdx))
				 .fetchOne();
		
		return result;
	}

	@Override
	public List<ProjectEntity> getUserProjects(UserDto loginUser) {
		BooleanBuilder builder = new BooleanBuilder();
		QueryResults<ProjectEntity> result = queryFactory
				  .select(Projections.fields(
						  ProjectEntity.class,
						  projectEntity.id, 
						  projectEntity.projectName,
						  projectEntity.description, 
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.createdAt, "%Y-%m-%d"),
								  "createdAt"),
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.updatedAt, "%Y-%m-%d %H:%i:%s"),
								  "updatedAt")
				  ))
				  .from(projectEntity)
				  .where(projectEntity.id.in(
						 JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.userId.eq(loginUser.getUserId()), builder)), projectEntity.deletedYn.eq("N")
				  ).fetchResults();
		return result.getResults();
	}
}
