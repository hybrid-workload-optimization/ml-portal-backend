package kr.co.strato.domain.cluster.repository;

import static kr.co.strato.domain.cluster.model.QClusterEntity.clusterEntity;
import static kr.co.strato.domain.project.model.QProjectClusterEntity.projectClusterEntity;
import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;
import static kr.co.strato.domain.user.model.QUserEntity.userEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.user.model.UserEntity;
import kr.co.strato.domain.user.model.UserRoleEntity;
import kr.co.strato.portal.setting.model.UserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Repository
public class ClusterUserRepositoryCustomImpl implements ClusterUserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	

	@Override
	public List<ClusterEntity> getUserClusterList(UserDto loginUser) {
		JPAQuery<ClusterEntity> query =  queryFactory
				.select(clusterEntity)
				  .from(clusterEntity);
		
		if(loginUser != null 
				&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN)
				&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
			query = query.where(clusterEntity.clusterIdx.in(
					JPAExpressions.select(projectClusterEntity.clusterIdx).from(projectClusterEntity).where(projectClusterEntity.projectIdx.in(
							JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.userId.eq(loginUser.getUserId()))
					))
			  ));
	    }
		query.orderBy(clusterEntity.createdAt.desc());
		
		List<ClusterEntity> result = query.fetch();		
		return result;
	}
	
	
	@Override
	public List<ClusterEntity> getUserClusterListForDevops(UserDto loginUser) {
		JPAQuery<ClusterEntity> query =  queryFactory
				.select(clusterEntity)
				  .from(clusterEntity);
		
		if(loginUser != null 
				&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_PORTAL_ADMIN)
				&& !loginUser.getUserRole().getUserRoleCode().equals(UserRoleEntity.ROLE_CODE_SYSTEM_ADMIN)) {
			query = query.where(clusterEntity.clusterIdx.in(
					JPAExpressions.select(projectClusterEntity.clusterIdx).from(projectClusterEntity).where(projectClusterEntity.projectIdx.in(
							JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.userId.eq(loginUser.getUserId()))
					))
					
			  ).or(clusterEntity.createUserId.eq(loginUser.getUserId())));
	    }
		query.orderBy(clusterEntity.createdAt.desc());
		
		List<ClusterEntity> result = query.fetch();
		return result;
	}


	@Override
	public Page<ClusterEntity> getUserClusterList(Pageable pageable, UserDto loginUser) {
		List<ClusterEntity> results = getUserClusterList(loginUser);		
		long total = results.size();
        return new PageImpl<>(results, pageable, total);
	}


	@Override
	public List<UserEntity> getClusterUsers(Long clusterIdx) {
		List<UserEntity> result = queryFactory
				.select(userEntity)
				 .from(projectClusterEntity)
				  .join(projectUserEntity).on(projectUserEntity.projectIdx.eq(projectClusterEntity.projectIdx))
				  .join(userEntity).on(userEntity.userId.eq(projectUserEntity.userId))
				  .where(projectClusterEntity.clusterIdx.eq(clusterIdx).and(userEntity.useYn.eq("Y")))
				  .fetch();
		
		return result;
	}
}