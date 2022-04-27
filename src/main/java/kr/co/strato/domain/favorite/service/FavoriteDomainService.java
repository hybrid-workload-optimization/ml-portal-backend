package kr.co.strato.domain.favorite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.favorite.model.FavoriteEntity;
import kr.co.strato.domain.favorite.repository.FavoriteRepository;
import kr.co.strato.domain.menu.model.MenuEntity;

@Service
public class FavoriteDomainService {
	
	@Autowired
	private FavoriteRepository favoriteRepository;
	
	
	/**
	 * 저장
	 * @param entity
	 * @return
	 */
	public Long register(FavoriteEntity entity) {
		favoriteRepository.save(entity);
		return entity.getId();
	}
	
	/**
	 * 삭제
	 * @param userId
	 * @param menuEntity
	 */
	public void delete(String userId, MenuEntity menuEntity) {
		favoriteRepository.deleteByUserIdAndMenu(userId, menuEntity);
	}

	/**
	 * 사용자 즐겨 찾기 목록 리턴.
	 * @param userId
	 * @return
	 */
	public List<FavoriteEntity> getList(String userId) {
		return favoriteRepository.getFavoriteList(userId);
	}
	
	
	public void deleteByUserId(String userId) {
		favoriteRepository.deleteByUserId(userId);
	}
	
}
