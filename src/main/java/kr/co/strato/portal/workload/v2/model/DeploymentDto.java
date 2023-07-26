package kr.co.strato.portal.workload.v2.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class DeploymentDto extends WorkloadCommonDto {
	private String image;
	private String strategy;
	private String selector;
	private Float maxSurge;
	private Float maxUnavailable;
	private Integer podUpdated;
	private Integer podReplicas;
	private Integer podReady;
	private String condition;
	private String replicaSetUid;
	private List<ReplicaSetDto> replicaSets;
}
