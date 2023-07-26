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
public class JobDto extends WorkloadCommonDto {
	private String image;	
	private Integer completed;
	private Integer parallel;	
	private Integer active;
	private Integer succeeded;	
	private List<PodDto> pods;
	private String pod;
}
