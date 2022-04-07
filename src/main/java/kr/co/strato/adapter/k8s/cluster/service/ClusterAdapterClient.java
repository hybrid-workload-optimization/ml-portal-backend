package kr.co.strato.adapter.k8s.cluster.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.strato.adapter.k8s.cluster.model.ClusterAdapterDto;

@FeignClient(name = "clusterAdapterClient", url = "${service.kubernetes-interface.url}")
public interface ClusterAdapterClient {
	
	/**
	 * k8s config 상세 정보
	 * 
	 * @param kubeConfigId
	 * @return
	 */
	@GetMapping("/kubeConfig")
	public String getCluster(@RequestParam("kubeConfigId") Long kubeConfigId);
	
	/**
	 * k8s config 등록
	 *  
	 * @param provider
	 * @param configContents
	 * @return
	 */
	@PostMapping("/kubeConfig")
    public String postCluster(@RequestBody ClusterAdapterDto clusterAdapterDto);

	/**
	 * k8s config 수정
	 * 
	 * @param provider
	 * @param configContents
	 * @param kubeConfigId
	 * @return
	 */
	@PutMapping("/kubeConfig")
	public boolean putCluster(@RequestBody ClusterAdapterDto clusterAdapterDto);
	
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
	
	/**
	 * k8s cluster (health + version)정보
	 * 
	 * @param kubeConfigId
	 * @return
	 */
	@GetMapping("/cluster/info")
	public String getClusterInfo(@RequestParam("kubeConfigId") Long kubeConfigId);
	
	/**
	 * k8s cluster health 정보
	 * @return
	 */
	@GetMapping("/cluster/health")
	public String getClusterHealth(@RequestParam("kubeConfigId") Long kubeConfigId);
	
	/**
	 * k8s cluster version 정보
	 * @return
	 */
	@GetMapping("/cluster/version")
	public String getClusterVersion(@RequestParam("kubeConfigId") Long kubeConfigId);
	
}
