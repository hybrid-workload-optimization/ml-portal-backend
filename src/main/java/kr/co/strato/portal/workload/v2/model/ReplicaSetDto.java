package kr.co.strato.portal.workload.v2.model;

import java.util.List;
import java.util.Map;

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
public class ReplicaSetDto extends WorkloadCommonDto {
	private Map<String, String>  selectors;
	private String image;
	private int runningPod;
	private int desiredPod;
	private List<PodDto> pods;
}
