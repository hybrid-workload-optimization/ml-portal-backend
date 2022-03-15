package kr.co.strato.adapter.k8s.common.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

@FeignClient(value="PodLog", url = "${service.kubernetes-interface.url}")
public interface PodProxy {

    @GetMapping("/pod/log/download")
    public @ResponseBody
    ResponseEntity<ByteArrayResource> getResourceLogFile(
    		@RequestParam("kubeConfigId") Long kubeConfigId,
            @RequestParam("namespace") String namespace,
            @RequestParam("name") String name);
}
