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
public class StatefulSetDto extends WorkloadCommonDto {
	 private String image;
	 private Integer replicas;
     private Integer readyReplicas;
}
