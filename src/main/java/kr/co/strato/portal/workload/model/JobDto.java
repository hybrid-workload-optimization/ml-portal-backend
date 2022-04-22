package kr.co.strato.portal.workload.model;

import java.util.HashMap;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class JobDto {
	private Long idx;
	private String name;
	private String uid;
	private String clusterName;
	private Long namespaceIdx;
	private String namespace;
	private String createdAt;
	private String image;
	private HashMap<String, Object> label;
	private String pod;
	
	private Integer completed;
	private Integer parallel;
	
	private Integer active;
	private Integer succeeded;
	private Long projectIdx;
	
	private List<PodDto.ResListDto> pods;
	
	
	
}
