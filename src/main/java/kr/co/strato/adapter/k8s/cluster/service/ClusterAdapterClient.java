package kr.co.strato.adapter.k8s.cluster.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "clusterAdapterClient", url = "${service.kubernetes-interface.url}")
public interface ClusterAdapterClient {
	
	@PostMapping("/kube-config")
    public String postCluster(@RequestParam("provider") String provider, @RequestParam("configContents") String configContents);

	@PutMapping("/kube-config")
	public String putCluster(@RequestParam("provider") String provider, @RequestParam("configContents") String configContents, @RequestParam("kubeConfigId") Long kubeConfigId);
	
}
