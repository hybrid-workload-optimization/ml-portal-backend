package kr.co.strato.portal.networking.v2.model;

import lombok.Getter;
import lombok.Setter;

public class NetworkCommonDto {

	@Getter
	@Setter
	public static class Search {
		private Long clusterIdx;
        private String name;
        private String namespace;
	}
}
