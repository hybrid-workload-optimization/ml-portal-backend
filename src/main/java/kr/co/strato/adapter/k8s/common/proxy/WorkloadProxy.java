package kr.co.strato.adapter.k8s.common.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;

@FeignClient(value="WorkloadProxy", url = "${service.kubernetes-interface.url}")
public interface WorkloadProxy {

    @PostMapping("/workload/list")
    public @ResponseBody String getWorkloadList(@RequestBody ResourceListSearchInfo listSearchInfo);
    
}