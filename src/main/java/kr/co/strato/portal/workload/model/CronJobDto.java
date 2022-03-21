package kr.co.strato.portal.workload.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CronJobDto {
	private Long idx;
	private String name;
	private String uid;
	private Long namespaceIdx;
	private String namespaceName;
}
