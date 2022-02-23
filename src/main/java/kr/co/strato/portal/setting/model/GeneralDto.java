package kr.co.strato.portal.setting.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class GeneralDto {
	private Long idx;
	private String type;
	private String key;
	private String value;
	private String description;
}
