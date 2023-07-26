package kr.co.strato.portal.workload.v2.model;

import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Setter
@Getter
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class WorkloadCommonDto {
	private String uid;
	private String name;
	private String namespace;
	private Map<String, String> labels;
	private Map<String, String> annotations;
	private String kind;
	private String createdAt;
}
