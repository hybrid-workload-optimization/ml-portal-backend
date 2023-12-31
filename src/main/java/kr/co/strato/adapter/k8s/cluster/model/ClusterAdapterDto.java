package kr.co.strato.adapter.k8s.cluster.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ClusterAdapterDto {

	private Long kubeConfigId;
	
	private String provider;
	
	private String configContents;
	
}
