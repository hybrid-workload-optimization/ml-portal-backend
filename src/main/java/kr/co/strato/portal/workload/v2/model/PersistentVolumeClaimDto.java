package kr.co.strato.portal.workload.v2.model;

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
public class PersistentVolumeClaimDto extends WorkloadCommonDto {
	private String status;
	private String capacity;
	private String accessType;
	private String storageClass;
	private String createdAt;
}
