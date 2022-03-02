package kr.co.strato.adapter.k8s.cluster.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class ClusterInfoAdapterDto {

	private ClusterHealthAdapterDto clusterHealth;
	
	private String kubeletVersion;
	
}
