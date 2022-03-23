package kr.co.strato.adapter.cloud.cluster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import kr.co.strato.adapter.cloud.cluster.model.ClusterCloudDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterCloudService {

	@Autowired
	ClusterCloudClient clusterCloudClient;
	
	public boolean createCluster(ClusterCloudDto clusterCloudDto) throws Exception {
		log.debug("[Create Cluster] request : {}", clusterCloudDto.toString());
		boolean response = clusterCloudClient.createCluster(clusterCloudDto);
		log.debug("[Create Cluster] response : {}", response);
		
		return response;
	}
	
	public String scaleCluster(ClusterCloudDto clusterCloudDto) throws Exception {
		log.debug("[Scale Cluster] request : {}", clusterCloudDto.toString());
		String response = clusterCloudClient.scaleCluster(clusterCloudDto);
		log.debug("[Scale Cluster] response : {}", response);
		
		return response;
	}
	
	public String removeCluster(ClusterCloudDto clusterCloudDto) throws Exception {
		log.debug("[Remove Cluster] request : {}", clusterCloudDto.toString());
		String response = clusterCloudClient.removeCluster(clusterCloudDto);
		log.debug("[Remove Cluster] response : {}", response);
		
		return response;
	}
	
}
