package kr.co.strato.adapter.cloud.eks.proxy;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import kr.co.strato.adapter.cloud.eks.model.NodeJobArg;
import kr.co.strato.adapter.cloud.eks.model.NodeStatusRes;

@FeignClient(value="aws-interface", url = "${ml.interface.url.aws}")
public interface EKSInterfaceProxy {
	
	@PostMapping("/api/v1/cloud/server/status")
	public List<NodeStatusRes> statusNode(@RequestBody NodeJobArg param);
	
	@PostMapping("/api/v1/cloud/server/start")
	public boolean startNode(@RequestBody NodeJobArg.Job param);
	
	@PostMapping("/api/v1/cloud/server/stop")
	public boolean stopNode(@RequestBody NodeJobArg.Job param);
	
}
