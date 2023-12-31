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
    public boolean createCluster(@RequestBody ClusterCloudDto clusterCloudDto);

	/**
	 * kubespray cluster 삭제
	 * 
	 * @param clusterCloudDto
	 * @return
	 */
	@DeleteMapping("/cluster/remove")
	public boolean removeCluster(@RequestBody ClusterCloudDto clusterCloudDto);
	
	/**
	 * kubespray cluster scale(out) 조정
	 * 
	 * @param clusterCloudDto
	 * @return
	 */
	@PostMapping("/cluster/scale")
	public boolean scaleOutCluster(@RequestBody ClusterCloudDto clusterCloudDto);
	
	/**
	 * kubespray cluster scale(in) 조정 - Node 삭제
	 * 
	 * @param clusterCloudDto
	 * @return
	 */
	@DeleteMapping("/cluster/removeNode")
	public boolean scaleInCluster(@RequestBody ClusterCloudDto clusterCloudDto);
}
