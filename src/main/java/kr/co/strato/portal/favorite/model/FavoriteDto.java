package kr.co.strato.portal.favorite.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FavoriteDto {

	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqCreateDto {
		// TODO validation체크
		private Long menuIdx;
	}

}
