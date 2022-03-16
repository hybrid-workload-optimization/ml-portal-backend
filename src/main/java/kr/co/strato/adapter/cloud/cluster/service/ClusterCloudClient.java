package kr.co.strato.adapter.cloud.cluster.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.strato.adapter.cloud.cluster.model.ClusterCloudDto;

@FeignClient(name = "clusterCloudClient", url = "${service.cloud-interface.url}")
public interface ClusterCloudClient {
	
	/**
	 * kubespray cluster 생성
	 * 
	 * @param clusterCloudDto
	 * @return
	 */
	@PostMapping("/cluster/create")
    public String createCluster(@RequestBody ClusterCloudDto clusterCloudDto);

	/**
	 * kubespray cluster 삭제
	 * 
	 * @param clusterCloudDto
	 * @return
	 */
	@DeleteMapping("/cluster/remove")
	public String removeCluster(@RequestBody ClusterCloudDto clusterCloudDto);
	
	/**
	 * kubespray cluster scale 조정
	 * 
	 * @param clusterCloudDto
	 * @return
	 */
	@PostMapping("/cluster/scale")
	public String scaleCluster(@RequestBody ClusterCloudDto clusterCloudDto);
	
}
