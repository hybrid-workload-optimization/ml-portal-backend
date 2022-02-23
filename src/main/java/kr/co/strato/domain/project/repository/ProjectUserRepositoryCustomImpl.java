package kr.co.strato.domain.project.repository;

import static kr.co.strato.domain.project.model.QProjectUserEntity.projectUserEntity;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import javax.persistence.EntityManager;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
//@RequiredArgsConstructor
@Repository
public class ProjectUserRepositoryCustomImpl implements ProjectUserRepositoryCustom {

	private final JPAQueryFactory queryFactory;
	
	public ProjectUserRepositoryCustomImpl(EntityManager entityManager) {
		this.queryFactory = new JPAQueryFactory(entityManager);
	}
	
	@Override
	public List<ProjectUserDto> getProjectByUserId(String userId) {
		
		/*List<ProjectUserDto> result = queryFactory
				.select(Projections.fields(ProjectUserDto.class, projectUserEntity.projectIdx.as("projectIdx")
						))
				  //.select(projectUserEntity)
				  .from(projectUserEntity)
				  //.leftJoin(projectEntity.id, projectUserEntity)
				  //.where(condition(userId, projectUserEntity.id::eq))
				  //.leftJoin(projectUserEntity).on(projectEntity.id.eq(projectUserEntity.id))
	              .fetch();*/
		
		return null;
	}
	
	private <T> BooleanExpression condition(T value, Function<T, BooleanExpression> function) {
		return Optional.ofNullable(value).map(function).orElse(null);
	}
}
