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
public class CronJobDto extends WorkloadCommonDto {
	private String schedule;
	private String pause;
	private String lastSchedule;
	private String concurrencyPolicy;
	private Integer active;
	
	private List<JobDto> activeJobs;
	private List<JobDto> inactiveJobs;	
}
