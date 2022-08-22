package kr.co.strato.domain.machineLearning.repository;

import static kr.co.strato.domain.project.model.QProjectClusterEntity.projectClusterEntity;
import static kr.co.strato.domain.project.model.QProjectEntity.projectEntity;
import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;
import static kr.co.strato.domain.user.model.QUserRoleEntity.userRoleEntity;

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
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.domain.machineLearning.model.QMLEntity;
import kr.co.strato.domain.machineLearning.model.QMLProjectEntity;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.machineLearning.model.MLProjectDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class CustomMLProjectRepositoryImpl implements CustomMLProjectRepository {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public PageImpl<MLProjectDto> getProjectList(UserDto loginUser, Pageable pageable, ProjectDto param) throws Exception {
		BooleanBuilder builder = new BooleanBuilder();
	    if(param.getProjectName() != null && !"".equals(param.getProjectName())) {
	    	builder.and(projectEntity.projectName.contains(param.getProjectName()));
	    }
	    
	    JPAQuery<MLProjectDto> query = queryFactory
				  .select(Projections.fields(
						  MLProjectDto.class,
						  projectEntity.id, 
						  projectEntity.projectName,
						  projectEntity.description, 
						  ExpressionUtils.as(
								  JPAExpressions.select(QMLProjectEntity.mLProjectEntity.id.count())
	                                            .from(QMLProjectEntity.mLProjectEntity)
	                                            .where(QMLProjectEntity.mLProjectEntity.project.id.eq(projectEntity.id)),
	                              "mlCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectClusterEntity.clusterIdx.count())
	                                            .from(projectClusterEntity)
	                                            .where(projectClusterEntity.projectIdx.eq(projectEntity.id)),
	                              "clusterCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.userId.count())
	                                            .from(projectUserEntity)
	                                            .join(userEntity).on(projectUserEntity.userId.eq(userEntity.userId))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), userEntity.useYn.eq("Y")),
	                              "userCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.userId)
	                                            .from(projectUserEntity)
	                                            //.where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.projectUserRole.eq("PM")),
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.userRoleIdx.eq(ProjectUserEntity.PROJECT_MANAGER_ROLE_IDX)),
	                              "projectPmId"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.userName)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.userId))
	                                            //.where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.userRoleIdx.eq("PM")),
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.userRoleIdx.eq(ProjectUserEntity.PROJECT_MANAGER_ROLE_IDX)),
	                              "projectPmName"),
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.createdAt, "%Y-%m-%d"),
								  "createdAt"),
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.updatedAt, "%Y-%m-%d %H:%i:%s"),
								  "updatedAt")
				  ))
				  .from(projectEntity)
				  .where(projectEntity.deletedYn.eq("N"))
				  .offset(pageable.getOffset())
                  .limit(pageable.getPageSize())
	    		  .orderBy(projectEntity.createdAt.desc());
	    
	    
	    if(loginUser != null && !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN) 
	    		&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
	    	query = query.where(projectEntity.id.in(
					 JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.userId.eq(loginUser.getUserId()), builder))
			);
	    }
	    
	    QueryResults<MLProjectDto> result = query.fetchResults();
	    
		
		
		List<MLProjectDto> resultList = result.getResults();
		if(resultList != null && resultList.size() > 0) {
			for(MLProjectDto dto : resultList) {
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
		
		return new PageImpl<MLProjectDto>(resultList, pageable, total);
	}
	
	
	@Override
	public MLProjectDto getProjectDetail(Long projectIdx) {
		
		BooleanBuilder builder = new BooleanBuilder();
	    if(projectIdx != null) {
	    	builder.and(projectEntity.id.eq(projectIdx));
	    }
		
	    MLProjectDto result = queryFactory
				  .select(Projections.fields(
						  MLProjectDto.class,
						  projectEntity.id, 
						  projectEntity.projectName,
						  projectEntity.description, 
						  ExpressionUtils.as(
								  JPAExpressions.select(QMLProjectEntity.mLProjectEntity.id.count())
	                                            .from(QMLProjectEntity.mLProjectEntity)
	                                            .where(QMLProjectEntity.mLProjectEntity.project.id.eq(projectEntity.id)),
	                              "mlCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectClusterEntity.clusterIdx.count())
	                                            .from(projectClusterEntity)
	                                            .where(projectClusterEntity.projectIdx.eq(projectEntity.id)),
	                              "clusterCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.userId.count())
	                                            .from(projectUserEntity)
	                                            .join(userEntity).on(projectUserEntity.userId.eq(userEntity.userId))
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id), userEntity.useYn.eq("Y")),
	                              "userCount"),
						  ExpressionUtils.as(
								  JPAExpressions.select(projectUserEntity.userId)
	                                            .from(projectUserEntity)
	                                            //.where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.userRoleIdx.eq(5L)),
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id).and(projectUserEntity.userRoleIdx.eq(JPAExpressions.select(userRoleEntity.id).from(userRoleEntity).where(userRoleEntity.userRoleCode.eq(ProjectUserEntity.PROJECT_MANAGER))))),
	                              "projectPmId"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.userName)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.userId))
	                                            //.where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.userRoleIdx.eq(5L)),
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id).and(projectUserEntity.userRoleIdx.eq(JPAExpressions.select(userRoleEntity.id).from(userRoleEntity).where(userRoleEntity.userRoleCode.eq(ProjectUserEntity.PROJECT_MANAGER))))),
	                              "projectPmName"),
						  ExpressionUtils.as(
								  JPAExpressions.select(userEntity.email)
	                                            .from(userEntity)
	                                            .join(projectUserEntity).on(userEntity.userId.eq(projectUserEntity.userId))
	                                            //.where(projectUserEntity.projectIdx.eq(projectEntity.id), projectUserEntity.userRoleIdx.eq(5L)),
	                                            .where(projectUserEntity.projectIdx.eq(projectEntity.id).and(projectUserEntity.userRoleIdx.eq(JPAExpressions.select(userRoleEntity.id).from(userRoleEntity).where(userRoleEntity.userRoleCode.eq(ProjectUserEntity.PROJECT_MANAGER))))),
	                              "projectPmEmail"),
						  
						  ExpressionUtils.as(
								  Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectEntity.createdAt, "%Y-%m-%d %H:%i"),
								  "createdAt"
								)
				  ))
				  .from(projectEntity)
				  .where(projectEntity.id.eq(projectIdx)
				  ).fetchOne();
		return result;
	}
	
	@Override
	public List<MLEntity> getProjectMlList(Long projectIdx) {
		QMLProjectEntity qProjectEntity = QMLProjectEntity.mLProjectEntity;
		
		QMLEntity qEntity = QMLEntity.mLEntity;
		
		BooleanBuilder builder = new BooleanBuilder();
		builder.and(qProjectEntity.project.id.eq(projectIdx));

        QueryResults<MLEntity> results = queryFactory
        		.select(qEntity)
                .from(qProjectEntity)
                .join(qEntity).on(qProjectEntity.ml.id.eq(qEntity.id))
                .where(builder)
                .orderBy(qEntity.id.desc())
                .fetchResults();

        List<MLEntity> content = results.getResults();
		return content;
	}
}
