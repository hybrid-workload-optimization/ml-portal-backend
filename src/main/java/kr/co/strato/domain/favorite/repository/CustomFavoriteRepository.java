package kr.co.strato.domain.favorite.repository;

import java.util.List;

import kr.co.strato.domain.favorite.model.FavoriteEntity;

public interface CustomFavoriteRepository {
	
	/**
	 * 즐겨찾기 목록 리턴.
	 * @param userId
	 * @return
	 */
	public List<FavoriteEntity> getFavoriteList(String userId);
}
