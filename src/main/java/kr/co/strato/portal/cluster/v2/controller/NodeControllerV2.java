package kr.co.strato.portal.cluster.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v2.model.NodeDto;
import kr.co.strato.portal.cluster.v2.service.NodeService;

@Api(tags = {"Node API V2"})
@RequestMapping("/api/v2/node")
@RestController
public class NodeControllerV2 {
	
	@Autowired
	private NodeService nodeService;

	@Operation(summary = "Node 리스트 조회", description = "Node 리스트를 조회한다.")
	@GetMapping("/{clusterIdx}/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<NodeDto.ListDto>> getNodeList(@PathVariable(required = true) Long clusterIdx) {
        List<NodeDto.ListDto> list = nodeService.getListForClusterIdx(clusterIdx);
        return new ResponseWrapper<>(list);
    }
	
	@Operation(summary = "Node 상세 조회", description = "Node 상세 정보를 조회한다.")
	@GetMapping("/{clusterIdx}/{nodeName}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<NodeDto.DetailDto> getNodeDetail(
    		@PathVariable(required = true) Long clusterIdx,
    		@PathVariable(required = true) String nodeName) {
		NodeDto.DetailDto node = nodeService.getNode(clusterIdx, nodeName);
        return new ResponseWrapper<>(node);
    }
}
