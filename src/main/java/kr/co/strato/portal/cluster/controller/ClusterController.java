package kr.co.strato.portal.cluster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.cluster.service.ClusterService;

@RestController
public class ClusterController {

	@Autowired
	ClusterService clusterService;
	
	@GetMapping("/api/v1/clusters")
    public ResponseWrapper<Page<ClusterDto>> getCluterList(PageRequest pageRequest){
        Page<ClusterDto> results = clusterService.getClusterList(pageRequest.of());
        return new ResponseWrapper<>(results);
    }
	
}
