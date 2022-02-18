package kr.co.strato.adapter.k8s.cluster.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class ClusterAdapterService {

	@Autowired
	ClusterAdapterClient clusterAdapterClient;
	
	public ClusterAdapterDto registerCluster(ClusterAdapterDto clusterAdapterDto) throws Exception {
		String response = clusterAdapterClient.postCluster(clusterAdapterDto.getProvider(), clusterAdapterDto.getConfigContents());
			
		ObjectMapper mapper = new ObjectMapper();
		ClusterAdapterDto result = mapper.readValue(response, new TypeReference<ClusterAdapterDto>(){});
            
		return result;
	}
	
	public ClusterAdapterDto updateCluster(ClusterAdapterDto clusterAdapterDto) throws Exception {
		String response = clusterAdapterClient.putCluster(clusterAdapterDto.getProvider(), clusterAdapterDto.getConfigContents(), clusterAdapterDto.getKubeConfigId());
		
		ObjectMapper mapper = new ObjectMapper();
		ClusterAdapterDto result = mapper.readValue(response, new TypeReference<ClusterAdapterDto>(){});
            
		return result;
	}
}
