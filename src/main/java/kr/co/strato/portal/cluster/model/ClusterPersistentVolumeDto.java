package kr.co.strato.portal.cluster.model;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ClusterPersistentVolumeDto {
	private Long id;
	private String name;
	private String uid;
	private String ip;
	private String status;
	private LocalDateTime createdAt;
	private String accessMode;
	private String claim;
	private String reclaim;
	private String reclaimPolicy;
	private Long storageClassIdx;
	private String type;
	private String path;
	private String resourceName;
	private int size;
	private String annotation;
	private String label;
	private Long clusterIdx;
	
}
