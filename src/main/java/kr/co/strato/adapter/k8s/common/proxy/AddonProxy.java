package kr.co.strato.adapter.k8s.common.proxy;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.strato.portal.addon.model.Addon;


@FeignClient(value="Addon", url = "${service.kubernetes-interface.url}")
public interface AddonProxy {

    @GetMapping("/cluster/addons")
    public @ResponseBody List<Addon> getAddons(
    		@RequestParam("kubeConfigId") Long kubeConfigId);
    
    @GetMapping("/cluster/addon")
	public @ResponseBody Addon getAddon(
			@RequestParam("kubeConfigId") Long kubeConfigId, 
			@RequestParam("addonId") String addonId);
    
    @PostMapping("/cluster/addon")
	public @ResponseBody boolean install(
			@RequestBody Map<String, Object> param);
    
    @DeleteMapping("/cluster/addon")
	public @ResponseBody boolean uninstall(
			@RequestBody Map<String, Object> param);
	
}
