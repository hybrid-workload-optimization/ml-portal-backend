package kr.co.strato.portal.setting.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class SettingSelectorDto {
	private String id;
	private String text;
	private String value;
}
