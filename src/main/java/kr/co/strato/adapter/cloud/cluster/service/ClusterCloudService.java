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
	
	public boolean scaleOutCluster(ClusterCloudDto clusterCloudDto) throws Exception {
		log.debug("[Scale Out Cluster] request : {}", clusterCloudDto.toString());
		boolean response = clusterCloudClient.scaleOutCluster(clusterCloudDto);
		log.debug("[Scale Out Cluster] response : {}", response);
		
		return response;
	}
	
	public boolean scaleInCluster(ClusterCloudDto clusterCloudDto) throws Exception {
		log.debug("[Scale In Cluster] request : {}", clusterCloudDto.toString());
		boolean response = clusterCloudClient.scaleInCluster(clusterCloudDto);
		log.debug("[Scale In Cluster] response : {}", response);
		
		return response;
	}
	
	public boolean removeCluster(ClusterCloudDto clusterCloudDto) throws Exception {
		log.debug("[Remove Cluster] request : {}", clusterCloudDto.toString());
		boolean response = clusterCloudClient.removeCluster(clusterCloudDto);
		log.debug("[Remove Cluster] response : {}", response);
		
		return response;
	}
	
}
