package kr.co.strato.adapter.k8s.common.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;

@FeignClient(value="Common", url = "${service.kubernetes-interface.url}")
public interface CommonProxy {

    @PostMapping("/common/apply")
    public String apply(@RequestBody YamlApplyParam param);
    
    
    @PostMapping("/common/delete")
    public boolean delete(@RequestBody YamlApplyParam param);

}
