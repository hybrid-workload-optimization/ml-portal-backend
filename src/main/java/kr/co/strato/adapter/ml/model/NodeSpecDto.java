package kr.co.strato.adapter.ml.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class NodeSpecDto {
	private String name;
	private String product;
	private String instance;
	private float cpu;
	private float memory;
	private float storage;
	private int count;
	private float gpu;
	private float gpuMemory;
	private float cost;
}
