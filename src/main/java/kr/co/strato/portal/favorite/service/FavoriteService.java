package kr.co.strato.portal.favorite.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.domain.favorite.model.FavoriteEntity;
import kr.co.strato.domain.favorite.service.FavoriteDomainService;
import kr.co.strato.domain.menu.model.MenuEntity;
import kr.co.strato.domain.menu.service.MenuDomainService;
import kr.co.strato.global.error.exception.InternalServerException;
import kr.co.strato.global.util.DateUtil;
import kr.co.strato.portal.favorite.model.FavoriteDto;

@Service
public class FavoriteService {

	@Autowired
	private FavoriteDomainService favoriteDomainService;
	
	@Autowired
	private MenuDomainService menuDomainService;
	
	/**
	 * 즐겨찾기 등록
	 * @param userId
	 * @param param
	 * @return
	 */
	public Long registerFavorite(String userId, FavoriteDto.ReqCreateDto param) {
		Long menuIdx = param.getMenuIdx();
		MenuEntity menuEntity = menuDomainService.getById(menuIdx);
		if(menuEntity != null) {
			FavoriteEntity entity = new FavoriteEntity();
			entity.setUserId(userId);
			entity.setMenu(menuEntity);
			entity.setCreatedAt(DateUtil.currentDateTime("yyyy-MM-dd HH:mm:ss"));
			return favoriteDomainService.register(entity);
		} else {
			throw new InternalServerException("Favorite 생성 실패, MenuEntity를 찾을 수 없습니다. Menu ID:" + menuIdx);
		}
	}
	
	/**
	 * 즐겨찾기 목록 반환.
	 * @param userId
	 * @return
	 */
	public List<FavoriteEntity> getFavoriteList(String userId) {
		return favoriteDomainService.getList(userId);
	}
	
	/**
	 * 즐겨찾기 삭제
	 * @param userId
	 * @param param
	 * @return
	 */
	public boolean deleteFavorite(String userId, FavoriteDto.ReqCreateDto param) {
		Long menuIdx = param.getMenuIdx();
		MenuEntity menuEntity = menuDomainService.getById(menuIdx);
		if(menuEntity != null) {
			favoriteDomainService.delete(userId, menuEntity);
			return true;
		}
		return false;
	}
}
