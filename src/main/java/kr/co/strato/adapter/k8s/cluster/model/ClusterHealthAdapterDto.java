package kr.co.strato.adapter.k8s.cluster.model;

import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClusterHealthAdapterDto {

	private String health;
	
	private List<String> problem;
	
}
