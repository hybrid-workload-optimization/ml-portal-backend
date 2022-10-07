package kr.co.strato.adapter.cloud.aks.proxy;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;

import feign.HeaderMap;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.cloud.common.proxy.InterfaceProxy;

@FeignClient(value="azure-interface", url = "${ml.interface.url.azure}")
public interface AKSInterfaceProxy extends InterfaceProxy {

	@Override
	@PostMapping("/api/v1/cloud/aks/provisioning")
    //public String provisioning(@HeaderMap Map<String, Object> header, @RequestBody Map<String, Object> param);
	public String provisioning(@RequestBody Map<String, Object> param);
	
	@Override
	@DeleteMapping("/api/v1/cloud/aks/delete")
	//public boolean delete(@HeaderMap Map<String, Object> header, @RequestBody Map<String, Object> param);
	public boolean delete(@RequestBody Map<String, Object> param);
	
	@Override
	@PostMapping("/api/v1/cloud/aks/scale")
	//public boolean scale(@HeaderMap Map<String, Object> header, @RequestBody Map<String, Object> param);
	public boolean scale(@RequestBody Map<String, Object> param);
	
	@Override
	@PostMapping("/api/v1/cloud/aks/modify")
	//public boolean modify(@HeaderMap Map<String, Object> header, @RequestBody Map<String, Object> param);
	public boolean modify(@RequestBody Map<String, Object> param);
}
