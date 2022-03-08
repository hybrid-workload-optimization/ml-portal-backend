package kr.co.strato.adapter.k8s.kubespray.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "kubesprayAdapterClient", url = "${service.cloud-interface.url}")
public interface KubesprayAdapterClient {
	
	/**
	 * k8s kubespray get versions 
	 * 
	 * @return array 
	 */
	@GetMapping("/kubespray/version")
	public List<String> getKubesprayVersion();
	
	/**
	 * k8s kubespray get setting data
	 * 
	 * @return array 
	 */
	@GetMapping("/kubespray/setting")
	public String getKubespraySetting(@RequestParam("version") String version);
	
}
