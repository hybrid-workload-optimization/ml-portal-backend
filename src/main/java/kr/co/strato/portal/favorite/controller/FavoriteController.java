package kr.co.strato.portal.favorite.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.domain.favorite.model.FavoriteEntity;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.favorite.model.FavoriteDto;
import kr.co.strato.portal.favorite.service.FavoriteService;
import kr.co.strato.portal.setting.model.UserDto;


@RestController
public class FavoriteController extends CommonController {

	@Autowired
	private FavoriteService favoriteService;

	
	/**
	 * favorite 리스트 반환
	 * @return
	 */
	@GetMapping("/api/v1/favorite/list")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<FavoriteEntity>> getFavoriteList() {
		UserDto user = getLoginUser();
		String userId = "hclee@strato.co.kr";
		List<FavoriteEntity> list = favoriteService.getFavoriteList(userId);
		return new ResponseWrapper<>(list);
	}
	
	
	/**
	 * favorite 등록
	 * @param param
	 * @return
	 */
	@PostMapping("/api/v1/favorite")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<Long> registerFavorite(@RequestBody FavoriteDto.ReqCreateDto param) {
		UserDto user = getLoginUser();
		String userId = "hclee@strato.co.kr";
		Long newId = favoriteService.registerFavorite(userId, param);
		return new ResponseWrapper<>(newId);
	}

	
	/**
	 * favorite 삭제
	 * @param param
	 * @return
	 */
	@DeleteMapping("/api/v1/favorite")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteFavorite(@RequestBody FavoriteDto.ReqCreateDto param) {
		UserDto user = getLoginUser();
		String userId = "hclee@strato.co.kr";
		boolean isDeleted = favoriteService.deleteFavorite(userId, param);
		return new ResponseWrapper<>(isDeleted);
	}	
	
}
