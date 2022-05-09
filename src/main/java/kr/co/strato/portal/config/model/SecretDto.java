package kr.co.strato.portal.config.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SecretDto {

	private Long projectIdx;
	private Long clusterIdx;
	private Long namespaceIdx;
	private Long secretIdx;
	private String yaml;
	
	@Getter
	@Setter
	public static class List {
		private Long id;
		private String name;
		private String namespace;
		private String label;
		private String age;
	}
	
	@Getter
	@Setter
	public static class Detail {
		private Long id;
		private String name;
		private String namespace;
		private String uid;
		private String label;
		private String data;
		private String createdAt;
		private Long projectIdx;
		private String clusterName;
	}
	
	@Getter
	@Setter
	public static class Search {
		private Long projectIdx;
		private Long clusterIdx;
		private Long namespaceIdx;
	}
}
