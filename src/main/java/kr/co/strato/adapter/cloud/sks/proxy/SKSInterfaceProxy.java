package kr.co.strato.adapter.cloud.sks.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.strato.adapter.cloud.sks.model.VSphereVMAction;
import kr.co.strato.adapter.cloud.sks.model.VSphereVMPower;

@FeignClient(value="sks-interface", url = "${ml.interface.url.sks}")
public interface SKSInterfaceProxy {
	
	@PostMapping("/api/v1/cloud/sks/vsphere/vm-action")
	public boolean powerAction(@RequestBody VSphereVMAction action);

	@PostMapping("/api/v1/cloud/sks/vsphere/vm-power")
	public List<VSphereVMPower> powerState(@RequestBody List<String> vmNames);
}
