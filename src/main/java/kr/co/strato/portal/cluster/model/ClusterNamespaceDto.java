package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterNamespaceDto {
	private Long id;
	private String name;
	private String uid;
	private String ip;
	private String status;
	private LocalDateTime createdAt;
	private Long clusterIdx;
	private String annotation;
	private String label;
	private String condition;
}
