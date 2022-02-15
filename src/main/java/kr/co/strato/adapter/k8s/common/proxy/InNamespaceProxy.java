package kr.co.strato.adapter.k8s.common.proxy;

import kr.co.strato.adapter.k8s.common.model.ClusterResourceInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value="InNamespace", url = "${service.kubernetes-interface.url}")
public interface InNamespaceProxy {

    @PostMapping("/inNamespace/{resourceType}/listStr")
    public @ResponseBody
    String getResourceList(
            @PathVariable("resourceType") String resourceType,
            @RequestBody ResourceListSearchInfo listSearchInfo);

    @GetMapping("/inNamespace/{resourceType}/detailStr")
    public @ResponseBody String getResource(
            @PathVariable("resourceType") String resourceType,
            @RequestParam("kubeConfigId") Integer kubeConfigId,
            @RequestParam("namespace") String namespace,
            @RequestParam("name") String name);

    @GetMapping("/inNamespace/{resourceType}/yaml")
    public String getResourceYaml(
            @PathVariable("resourceType") String resourceType,
            @RequestParam("kubeConfigId") Integer kubeConfigId,
            @RequestParam("namespace") String namespace,
            @RequestParam("name") String name);

    @DeleteMapping("/inNamespace/{resourceType}")
    public boolean deleteResource(
            @PathVariable("resourceType") String resourceType,
            @RequestBody ClusterResourceInfo resInfo);
}
