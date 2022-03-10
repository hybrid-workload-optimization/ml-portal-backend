package kr.co.strato.adapter.k8s.kubespray.service;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.strato.adapter.k8s.kubespray.model.KubesprayDto;

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
	 * @param String version
	 * @return String jsonArray
	 */
	@GetMapping("/kubespray/setting")
	public String getKubespraySetting(@RequestParam("version") String version);
	
	/**
	 * k8s kubespray put setting data 
	 * 
	 * @param version
	 * @param data (update object)
	 * @return boolean 
	 */
	@PutMapping("/kubespray/setting")
	public boolean patchKubespraySetting(@RequestBody KubesprayDto kubesprayDto);
}
