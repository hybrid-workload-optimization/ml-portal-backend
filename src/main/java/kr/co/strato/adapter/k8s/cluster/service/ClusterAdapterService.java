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
		String response = null;
		
		response = clusterAdapterClient.postCluster(clusterAdapterDto.getProvider(), clusterAdapterDto.getConfigContents());
		log.debug("[Register Cluster] response : {}", response);
		
		return response;
	}
	
	public String updateCluster(ClusterAdapterDto clusterAdapterDto) throws Exception {
		String response = clusterAdapterClient.putCluster(clusterAdapterDto.getProvider(), clusterAdapterDto.getConfigContents(), clusterAdapterDto.getKubeConfigId());
		log.debug("[Update Cluster] response : {}", response);
		
		return response;
	}
}
