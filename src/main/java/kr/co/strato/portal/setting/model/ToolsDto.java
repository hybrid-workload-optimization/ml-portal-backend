package kr.co.strato.portal.setting.model;

import java.util.HashMap;
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

	@Getter
	@Setter
	@ToString
	public static class ViewDto {
		private Long idx;
		private String type;
		private String key;
		private String value;
		private String description;
		
		private List<SettingSelectorDto> kubesprayVersions;
	}
	
	@Getter
	@Setter
	@ToString
	public static class ReqViewDto {
		private Long idx;
		private String type;
		private String key;
		private String value;
	}
	
	@Getter
	@Setter
	@ToString
	public static class ReqRegistDto {
		private String type;
		private String key;
		private String value;
		private String description;
		
		HashMap<String, String> setting;
	}
	
	@Getter
	@Setter
	@ToString
	public static class ReqModifyDto {
		private Long idx;
		private String type;
		private String key;
		private String value;
		private String description;
		
		HashMap<String, String> setting;
	}
	
}
