package kr.co.strato.adapter.cloud.eks.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class NodeJobArg {

	private String clusterName;
	private String region;
	
	@Data
	public static class Job extends NodeJobArg {
		private List<String> instanceIds;
	}
	
}
