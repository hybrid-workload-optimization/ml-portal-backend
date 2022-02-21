package kr.co.strato.adapter.k8s.cluster.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;

@FeignClient(name = "clusterAdapterClient", url = "${service.kubernetes-interface.url}")
public interface ClusterAdapterClient {
	
	/**
	 * k8s config 등록
	 *  
	 * @param provider
	 * @param configContents
	 * @return
	 */
	@PostMapping("/kubeConfig")
    public String postCluster(@RequestParam("provider") String provider, @RequestParam("configContents") String configContents);

	/**
	 * k8s config 수정
	 * 
	 * @param provider
	 * @param configContents
	 * @param kubeConfigId
	 * @return
	 */
	@PutMapping("/kubeConfig")
	public boolean putCluster(@RequestParam("provider") String provider, @RequestParam("configContents") String configContents, @RequestParam("kubeConfigId") Long kubeConfigId);
	
	/**
	 * k8s config 삭제
	 * 
	 * @param kubeConfigId
	 * @return
	 */
	@DeleteMapping("/kubeConfig")
	public boolean deleteCluster(@RequestParam("kubeConfigId") Long kubeConfigId);
	
	/**
	 * k8s 연결 테스트
	 *  
	 * @param configContents
	 * @return
	 */
	@PostMapping("/kubeConfig/checkConnection")
	public boolean isClusterConnection(@RequestBody ClusterAdapterDto clusterAdapterDto);
	
}
