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
public class PodSpecDto {
	private String name;
	private float cpu;
	private float memory;
	private float storage;
	private float gpu;
	private float gpuMemory;
}
