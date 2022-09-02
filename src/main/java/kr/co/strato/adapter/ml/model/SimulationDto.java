package kr.co.strato.adapter.ml.model;

import java.util.List;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class SimulationDto {
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Summary {
		private float cpu;
		private float memory;
		private float storage;
		private float gpu;
		private float gpuMemory;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class PodItem {
		private Integer count;
		private List<PodSpecDto> items;
		private Summary summary;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class UnusedNodeItem {
		private Integer count;
		private List<NodeSpecDto> items;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class UnusedPodItem {
		private Integer count;
		private List<PodSpecDto> items;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class SimulationItem {
		private NodeSpecDto node;
		private PodItem pod;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class Unused {
		private UnusedNodeItem node;
		private UnusedPodItem pod;
	}
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ReqSimulationDto {
		private String model;
		private List<PodSpecDto> podSpec;
		private NodeSpecDto nodeSpec;
	}
	
	
	
	@Getter
	@Setter
	@NoArgsConstructor
	public static class ResSimulationDto {
		private String model;
		private boolean isValid;
		private Integer count;
		private List<SimulationItem> items;
		private Unused unused;
	}
	
}
