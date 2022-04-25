package kr.co.strato.adapter.k8s.ingressController.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import kr.co.strato.adapter.k8s.ingressController.model.CreateIngressControllerParam;
import kr.co.strato.adapter.k8s.ingressController.model.IngressController;

@FeignClient(value="IngressControllerProxy", url = "${service.kubernetes-interface.url}")
public interface IngressControllerProxy {
	
	@GetMapping("/ingressController/type")
	public String[] types(@RequestParam("provider") String provider);

	@PostMapping("/ingressController/create")
	public String create(@RequestBody CreateIngressControllerParam param);
	
	@PutMapping("/ingressController/update")
	public String update(@RequestBody CreateIngressControllerParam param);
	
	@DeleteMapping("/ingressController/remove")
	public boolean remove(@RequestBody CreateIngressControllerParam param);
	
	@GetMapping("/ingressController/list")
	public List<IngressController> list(@RequestParam("kubeConfigId") Long kubeConfigId);
}
