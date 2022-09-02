package kr.co.strato.adapter.ml.proxy;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.ml.model.CloudParamDto;

@FeignClient(value="azure-interface", url = "${ml.interface.url.azure}")
public interface AzureInterfaceProxy extends InterfaceProxy {

	@Override
	@PostMapping("/api/v1/cloud/aks/provisioning")
    public String provisioning(@RequestBody Map<String, Object> param);
	
	@Override
	@DeleteMapping("/api/v1/cloud/aks/delete/{clusterName}")
	public boolean delete(@PathVariable("clusterName") String clusterName);
	
	@Override
	@PostMapping("/api/v1/cloud/aks/scale")
	public boolean scale(@RequestBody CloudParamDto.ScaleArg arg);
}
