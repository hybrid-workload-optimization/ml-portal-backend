package kr.co.strato.portal.config.model;

import java.util.HashMap;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfigMapDto {

	private Long projectIdx;
	private Long clusterIdx;
	private Long namespaceIdx;
	private Long persistentVolumeClaimIdx;
	private String yaml;
	
	@Getter
	@Setter
	public static class List {
		private Long id;
		private String name;
		private String namespace;
		private String age;
	}
	
	@Getter
	@Setter
	public static class Detail {
		private Long id;
		private String name;
		private String namespace;
		private String uid;
		private String data;
		private String createdAt;
		private Long projectIdx;
	}
	
	@Getter
	@Setter
	public static class Search {
		private Long projectIdx;
		private Long clusterIdx;
		private Long namespaceIdx;
	}
}
