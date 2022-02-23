package kr.co.strato.portal.workload.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DeploymentDto {
	private Long idx;
	private String name;
	private String uid;
	private String image;
	private LocalDateTime createdAt;
	private String strategy;
	private String selector;
	private Float maxSurge;
	private Float maxUnavailable;
	private String annotation;
	private String label;
	private Integer podUpdated;
	private Integer podReplicas;
	private Integer podReady;
	private Long namespaceIdx;
	private String namespaceName;
	private String condition;
}
