package kr.co.strato.domain.favorite.repository;

import java.util.List;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.QueryResults;
import com.querydsl.jpa.impl.JPAQueryFactory;

import kr.co.strato.domain.favorite.model.FavoriteEntity;
import kr.co.strato.domain.favorite.model.QFavoriteEntity;
import kr.co.strato.domain.menu.model.QMenuEntity;

public class CustomFavoriteRepositoryImpl implements CustomFavoriteRepository {
	
	private final JPAQueryFactory jpaQueryFactory;

    public CustomFavoriteRepositoryImpl(JPAQueryFactory jpaQueryFactory) {
        this.jpaQueryFactory = jpaQueryFactory;
    }

	@Override
	public List<FavoriteEntity> getFavoriteList(String userId) {
		QFavoriteEntity qFavoriteEntity = QFavoriteEntity.favoriteEntity;
        QMenuEntity qMenuEntity = QMenuEntity.menuEntity;

        BooleanBuilder builder = new BooleanBuilder();
        builder.and(qFavoriteEntity.userId.eq(userId));


        QueryResults<FavoriteEntity> results =
                jpaQueryFactory
                        .select(qFavoriteEntity)
                        .from(qFavoriteEntity)
                        .join(qMenuEntity).on(qFavoriteEntity.menu.menuIdx.eq(qMenuEntity.menuIdx)).on(qMenuEntity.useYn.eq('Y'))
                        .where(builder)
                        .orderBy(qFavoriteEntity.id.desc())
                        .fetchResults();

        List<FavoriteEntity> content = results.getResults();
		return content;
	}

}
