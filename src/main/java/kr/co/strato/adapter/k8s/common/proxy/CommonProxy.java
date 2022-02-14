package kr.co.strato.adapter.k8s.common.proxy;

import io.fabric8.kubernetes.api.model.HasMetadata;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@FeignClient(value="Common", url = "${service.kubernetes-interface.url}")
public interface CommonProxy {

    @PostMapping("/apply")
    public List<HasMetadata> apply(@RequestBody YamlApplyParam param);

}
