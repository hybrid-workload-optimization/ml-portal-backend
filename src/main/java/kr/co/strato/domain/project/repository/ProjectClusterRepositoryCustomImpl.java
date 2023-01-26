package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.cluster.model.QClusterEntity.clusterEntity;
import static kr.co.strato.domain.node.model.QNodeEntity.nodeEntity;
import static kr.co.strato.domain.project.model.QProjectClusterEntity.projectClusterEntity;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ProjectClusterRepositoryCustomImpl implements ProjectClusterRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	@Override
	public List<ProjectClusterDto> getProjectClusterList(Long projectIdx) {
		
		List<ProjectClusterDto> result = queryFactory
				.select(Projections.fields(
						ProjectClusterDto.class,
						projectClusterEntity.clusterIdx, 
						projectClusterEntity.projectIdx,
						ExpressionUtils.as(
								  JPAExpressions.select(nodeEntity.cluster.clusterIdx.count())
	                                            .from(nodeEntity)
	                                            .where(nodeEntity.cluster.clusterIdx.eq(projectClusterEntity.clusterIdx)),
	                              "nodeCount"),
						clusterEntity.provider,
						clusterEntity.providerVersion, 
						clusterEntity.clusterName,
						clusterEntity.description,
						clusterEntity.provisioningType,
						clusterEntity.provisioningStatus,
						/*ExpressionUtils.as(
							Expressions.stringTemplate("DATE_FORMAT({0}, {1})", clusterEntity.createdAt, "%Y-%m-%d %H:%i"),
							"createdAt"
						)*/
						ExpressionUtils.as(
							Expressions.stringTemplate("DATE_FORMAT({0}, {1})", projectClusterEntity.addedAt, "%Y-%m-%d %H:%i"),
							"addedAt"
						)
				  ))
				  .from(projectClusterEntity)
				  .join(clusterEntity).on(projectClusterEntity.clusterIdx.eq(clusterEntity.clusterIdx))
				  .where(projectClusterEntity.projectIdx.eq(projectIdx))
				  .fetch();
		
		return result;
	}
	
	@Override
	public List<ClusterEntity> getProjectClusterListExceptUse(Long projectIdx) {
		
		List<ClusterEntity> result = queryFactory
				.select(clusterEntity)
				  .from(clusterEntity)
				  .where(clusterEntity.clusterIdx.notIn(
						JPAExpressions.select(projectClusterEntity.clusterIdx).from(projectClusterEntity).where(projectClusterEntity.projectIdx.eq(projectIdx))
				  ))
				  .fetch();
		
		return result;
	}
	
	@Override
	public List<ClusterEntity> getProjecClusterListByNotUsedClusters(UserDto loginUser) {

		BooleanBuilder builder = new BooleanBuilder();
		
		builder.and(clusterEntity.clusterIdx.notIn(
				JPAExpressions.select(projectClusterEntity.clusterIdx).from(projectClusterEntity)
		));
		
		if(loginUser != null && !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN) 
	    		&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
			//시스템 어드민 , 포탈 어드민이 아닌 일반 사용자인 경우
			//본인이 생성한 클러스터만 보이도록 수정.
			builder.and(clusterEntity.createUserId.eq(loginUser.getUserId()));
		}
		
		List<ClusterEntity> result = queryFactory
				.select(clusterEntity)
				  .from(clusterEntity)
				  .where(builder)
				  .fetch();
		
		return result;
	}
}