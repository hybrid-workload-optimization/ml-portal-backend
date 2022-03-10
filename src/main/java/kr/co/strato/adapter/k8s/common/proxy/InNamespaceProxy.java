package kr.co.strato.adapter.k8s.common.proxy;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import kr.co.strato.adapter.k8s.common.model.WorkloadResourceInfo;

@FeignClient(value="InNamespace", url = "${service.kubernetes-interface.url}")
public interface InNamespaceProxy {

    @PostMapping("/inNamespace/{resourceType}/list")
    public @ResponseBody
    String getResourceList(
            @PathVariable("resourceType") String resourceType,
            @RequestBody ResourceListSearchInfo listSearchInfo);

    @GetMapping("/inNamespace/{resourceType}")
    public @ResponseBody String getResource(
            @PathVariable("resourceType") String resourceType,
            @RequestParam("kubeConfigId") Long kubeConfigId,
            @RequestParam("namespace") String namespace,
            @RequestParam("name") String name);

    @GetMapping("/inNamespace/{resourceType}/yaml")
    public String getResourceYaml(
            @PathVariable("resourceType") String resourceType,
            @RequestParam("kubeConfigId") Long kubeConfigId,
            @RequestParam("namespace") String namespace,
            @RequestParam("name") String name);

    @DeleteMapping("/inNamespace/{resourceType}")
    public boolean deleteResource(
            @PathVariable("resourceType") String resourceType,
            @RequestBody WorkloadResourceInfo resInfo);
}
