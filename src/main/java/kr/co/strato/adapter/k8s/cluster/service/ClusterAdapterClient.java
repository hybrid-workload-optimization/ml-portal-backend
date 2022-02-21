package kr.co.strato.adapter.k8s.cluster.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "clusterAdapterClient", url = "${service.kubernetes-interface.url}")
public interface ClusterAdapterClient {
	
	@PostMapping("/kube-config")
    public String postCluster(@RequestParam("provider") String provider, @RequestParam("configContents") String configContents);

	@PutMapping("/kube-config")
	public boolean putCluster(@RequestParam("provider") String provider, @RequestParam("configContents") String configContents, @RequestParam("kubeConfigId") Long kubeConfigId);
	
	@DeleteMapping("/kube-config")
	public boolean deleteCluster(@RequestParam("kubeConfigId") Long kubeConfigId);
	
}
