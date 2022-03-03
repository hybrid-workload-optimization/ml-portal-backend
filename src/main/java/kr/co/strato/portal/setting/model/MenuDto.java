package kr.co.strato.portal.setting.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class MenuDto {
	private Long menuIdx;
	private String menuName;
	private String menuUrl;
	private Long parentMenuIdx;
	private Integer menuOrder;
	private Integer menuDepth;
	private String useYn;
	
	private String viewableYn;
	private String writableYn;
}
