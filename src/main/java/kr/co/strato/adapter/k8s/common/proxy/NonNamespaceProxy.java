package kr.co.strato.adapter.k8s.common.proxy;

import kr.co.strato.adapter.k8s.common.model.ClusterResourceInfo;
import kr.co.strato.adapter.k8s.common.model.ResourceListSearchInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

@FeignClient(value="NonNamespace", url = "${service.kubernetes-interface.url}")
public interface NonNamespaceProxy {

    @PostMapping("/nonNamespace/{resourceType}/listStr")
    public @ResponseBody
    String getResourceList(
            @PathVariable("resourceType") String resourceType,
            @RequestBody ResourceListSearchInfo listSearchInfo);

    @GetMapping("/nonNamespace/{resourceType}/detailStr")
    public @ResponseBody String getResource(
            @PathVariable("resourceType") String resourceType,
            @RequestParam("clusterId") Integer clusterId,
            @RequestParam("name") String name);

    @GetMapping("/nonNamespace/{resourceType}/yaml")
    public String getResourceYaml(
            @PathVariable("resourceType") String resourceType,
            @RequestParam("clusterId") Integer clusterId,
            @RequestParam("name") String name);

    @DeleteMapping("/nonNamespace/{resourceType}")
    public boolean deleteResource(
            @PathVariable("resourceType") String resourceType,
            @RequestBody ClusterResourceInfo resInfo);
}