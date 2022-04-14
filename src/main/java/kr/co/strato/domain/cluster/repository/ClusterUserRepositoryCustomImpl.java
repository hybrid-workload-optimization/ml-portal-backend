package kr.co.strato.domain.cluster.repository;

import static kr.co.strato.domain.cluster.model.QClusterEntity.clusterEntity;
import static kr.co.strato.domain.project.model.QProjectClusterEntity.projectClusterEntity;
import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.cluster.model.ClusterEntity;
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
		List<ClusterEntity> result = queryFactory
				.select(clusterEntity)
				  .from(clusterEntity)
				  .where(clusterEntity.clusterIdx.in(
						JPAExpressions.select(projectClusterEntity.clusterIdx).from(projectClusterEntity).where(projectClusterEntity.projectIdx.in(
								JPAExpressions.select(projectUserEntity.projectIdx).from(projectUserEntity).where(projectUserEntity.userId.eq(loginUser.getUserId()))
						))
				  ))
				  .fetch();
		
		return result;
	}


	@Override
	public Page<ClusterEntity> getUserClusterList(Pageable pageable, UserDto loginUser) {
		List<ClusterEntity> results = getUserClusterList(loginUser);		
		long total = results.size();
        return new PageImpl<>(results, pageable, total);
	}
}