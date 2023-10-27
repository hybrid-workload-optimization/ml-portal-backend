package kr.co.strato.portal.config.v2.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfigCommonDto {

	@Getter
	@Setter
	public static class Search {
		private Long clusterIdx;
        private String name;
        private String namespace;
	}
}
