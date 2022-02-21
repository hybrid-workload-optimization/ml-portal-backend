package kr.co.strato.adapter.k8s.cluster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterAdapterService {

	@Autowired
	ClusterAdapterClient clusterAdapterClient;
	
	public String registerCluster(ClusterAdapterDto clusterAdapterDto) throws Exception {
		log.debug("[Register Cluster] request : {}", clusterAdapterDto.toString());
		String response = clusterAdapterClient.postCluster(clusterAdapterDto.getProvider(), clusterAdapterDto.getConfigContents());
		log.debug("[Register Cluster] response : {}", response);
		
		return response;
	}
	
	public boolean updateCluster(ClusterAdapterDto clusterAdapterDto) throws Exception {
		log.debug("[Update Cluster] request : {}", clusterAdapterDto.toString());
		boolean response = clusterAdapterClient.putCluster(clusterAdapterDto.getProvider(), clusterAdapterDto.getConfigContents(), clusterAdapterDto.getKubeConfigId());
		log.debug("[Update Cluster] response : {}", response);
		
		return response;
	}
	
	public boolean deleteCluster(Long kubeConfigId) throws Exception {
		log.debug("[Delete Cluster] request : {}", kubeConfigId);
		boolean response = clusterAdapterClient.deleteCluster(kubeConfigId);
		log.debug("[Delete Cluster] response : {}", response);
		
		return response;
	}
	
	public boolean isClusterConnection(String configContents) throws Exception {
		log.debug("[Check Cluster Connection] request : {}", configContents);
		
		ClusterAdapterDto requestDto = ClusterAdapterDto.builder()
				.configContents(configContents)
				.build();
		
		boolean response = clusterAdapterClient.isClusterConnection(requestDto);
		log.debug("[Check Cluster Connection] response : {}", response);
		
		return response;
	}
}
