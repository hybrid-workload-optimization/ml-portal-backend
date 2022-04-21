package kr.co.strato.portal.alert.model;

import kr.co.strato.portal.work.model.WorkJob.WorkJobStatus;
import kr.co.strato.portal.work.model.WorkJob.WorkJobType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AlertDto {
	private Long alertIdx;
	private String clusterName;
	private Long clusterIdx;
	private WorkJobType workJobType;
	private WorkJobStatus workJobStatus;
	private String confirmYn;
	private String createdAt;
	private String updatedAt;
}
