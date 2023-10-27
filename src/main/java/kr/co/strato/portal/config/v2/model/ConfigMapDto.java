package kr.co.strato.portal.config.v2.model;

import kr.co.strato.portal.workload.v2.model.WorkloadCommonDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ConfigMapDto extends WorkloadCommonDto {
	private String data;
}
