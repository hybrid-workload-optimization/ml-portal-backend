package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterStorageClassDto {
	private Long id;
	private String name;
	private String uid;
	private String status;
	private LocalDateTime createdAt;
	private String provider;
	private String type;
	private Long clusterIdx;
	private String annotation;
	private String label;
	
}
