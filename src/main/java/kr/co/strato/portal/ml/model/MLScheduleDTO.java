package kr.co.strato.portal.ml.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class MLScheduleDTO {

	private String mlId;
	private Long clusterIdx;
//	private String clusterName;
//	private String region;

//	private String cron;
}
