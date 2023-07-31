package kr.co.strato.portal.cluster.v1.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.fabric8.kubernetes.api.model.Node;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.adapter.k8s.common.model.YamlApplyParam;
import kr.co.strato.global.error.exception.BadRequestException;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v1.model.ClusterNodeDto;
import kr.co.strato.portal.cluster.v1.service.ClusterNodeService;



@RestController
public class ClusterNodeController {

	@Autowired
	private ClusterNodeService nodeService;

	
	/**
	 * @param kubeConfigId
	 * @return
	 * k8s data 호출 및 db저장
	 */
	@GetMapping("/api/v1/cluster/clusterNodesListSet")
	@ResponseStatus(HttpStatus.OK)
	public List<Node> getClusterNodeList(@RequestParam Long kubeConfigId) {
		if (kubeConfigId == null) {
			throw new BadRequestException("kubeConfigId id is null");
		}
		return nodeService.getClusterNodeList(kubeConfigId);
	}
		
	
	@GetMapping("/api/v1/cluster/clusterNodes")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<ClusterNodeDto.ResListDto>> getClusterNodeList(PageRequest pageRequest, ClusterNodeDto.SearchParam searchParam){
        Page<ClusterNodeDto.ResListDto> results = nodeService.getClusterNodes(pageRequest.of(), searchParam);

        return new ResponseWrapper<>(results);
    }


	@PostMapping("/api/v1/cluster/registerClusterNode")
	@ResponseStatus(HttpStatus.CREATED)
	public ResponseWrapper<List<Long>> registerClusterNodes(@RequestBody ClusterNodeDto.ReqCreateDto yamlApplyParam) {
		List<Long> ids = null;
		
		try {
			 ids = nodeService.registerClusterNode(yamlApplyParam);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
		}
		
		return new ResponseWrapper<>(ids);
	}
	
	@DeleteMapping("/api/v1/cluster/deletClusterNode/{id}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteClusterNode(@PathVariable("id") Long id) {
		boolean isDeleted = nodeService.deleteClusterNode(id);
		
		return new ResponseWrapper<>(isDeleted);
	}
	
	@GetMapping("/api/v1/cluster/clusterNodes/{id:.+}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ClusterNodeDto.ResDetailDto> getClusterNodeDetail(@PathVariable("id") Long id) {
		ClusterNodeDto.ResDetailDto resBody = nodeService.getClusterNodeDetail(id);

		return new ResponseWrapper<>(resBody);
	}
	
	@GetMapping("/api/v1/cluster/clusterNodes/detail/{clusterIdx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<List<ClusterNodeDto.ResDetailDto>> getClusterNodesDetail(@PathVariable("clusterIdx") Long clusterIdx) {
		List<ClusterNodeDto.ResDetailDto> nodeDetails = nodeService.getClusterNodesDetail(clusterIdx);
		return new ResponseWrapper<>(nodeDetails);
	}
	
	@GetMapping("/api/v1/cluster/clusterNodesYaml")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getNodeYaml(@RequestParam Long kubeConfigId,String name) {
		String resBody = nodeService.getNodeYaml(kubeConfigId,name);

		return new ResponseWrapper<>(resBody);
	}
	
}
