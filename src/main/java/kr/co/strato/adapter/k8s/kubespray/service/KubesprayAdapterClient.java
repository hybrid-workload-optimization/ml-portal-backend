package kr.co.strato.adapter.k8s.kubespray.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(name = "kubesprayAdapterClient", url = "${service.cloud-interface.url}")
public interface KubesprayAdapterClient {
	
	/**
	 * k8s kubespray version get
	 * 
	 * @return array 
	 */
	@GetMapping("/kubespray/version")
	public List<String> getKubesprayVersion();
	
}
