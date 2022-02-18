package kr.co.strato.portal.cluster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
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
        Page<ClusterDto> results = null;
        
        try {
        	results = clusterService.getClusterList(pageRequest.of());
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//@TODO : work_history 등록 필요
		}
        
        return new ResponseWrapper<>(results);
    }
	
	@GetMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<ClusterDto> registerCluster(@PathVariable Long clusterIdx){
		ClusterDto result = null;
        
        try {
        	result = clusterService.getCluster(clusterIdx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//@TODO : work_history 등록 필요
		}
        
        return new ResponseWrapper<>(result);
    }
	
	@PostMapping("/api/v1/clusters")
    public ResponseWrapper<Long> registerCluster(@RequestBody ClusterDto clusterDto){
        Long clusterId = null;
        
        try {
			clusterService.registerCluster(clusterDto);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//@TODO : work_history 등록 필요
		}
        
        return new ResponseWrapper<>(clusterId);
    }

	@PutMapping("/api/v1/clusters")
    public ResponseWrapper<Long> updateCluster(@RequestBody ClusterDto clusterDto){
        Long clusterId = null;
        
        try {
			clusterService.updateCluster(clusterDto);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//@TODO : work_history 등록 필요
		}
        
        return new ResponseWrapper<>(clusterId);
    }
	
	@DeleteMapping("/api/v1/clusters/{clusterIdx}")
    public ResponseWrapper<ClusterDto> deleteCluster(@PathVariable Long clusterIdx){
		ClusterDto result = null;
        
        try {
        	clusterService.deleteCluster(clusterIdx);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			//@TODO : work_history 등록 필요
		}
        
        return new ResponseWrapper<>(result);
    }
}
