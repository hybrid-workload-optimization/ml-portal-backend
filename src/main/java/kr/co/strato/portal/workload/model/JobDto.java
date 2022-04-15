package kr.co.strato.portal.workload.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobDto {
	private Long idx;
	private String name;
	private String uid;
	private String clusterName;
	private Long namespaceIdx;
	private String namespace;
	private String createdAt;
	private String image;
	private String label;
	private String pod;
	
}
