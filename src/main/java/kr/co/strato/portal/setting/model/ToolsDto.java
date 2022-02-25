package kr.co.strato.portal.setting.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ToolsDto {
	private Long idx;
	private String type;
	private String key;
	private String value;
	private String description;
	
	private List<SettingSelectorDto> kubesprayVersions;
	
}
