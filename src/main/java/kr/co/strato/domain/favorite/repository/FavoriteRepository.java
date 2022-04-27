package kr.co.strato.domain.favorite.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.favorite.model.FavoriteEntity;
import kr.co.strato.domain.menu.model.MenuEntity;

public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long>, CustomFavoriteRepository {
	
	public List<FavoriteEntity> findByUserId(String userId);
	
	@Transactional
	public void deleteByUserIdAndMenu(String userId, MenuEntity menu);
	
	@Transactional
	public void deleteByUserId(String userId);
}
