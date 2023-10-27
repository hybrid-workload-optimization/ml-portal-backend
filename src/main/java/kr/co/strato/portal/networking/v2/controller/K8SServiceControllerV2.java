package kr.co.strato.portal.networking.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.networking.v2.model.NetworkCommonDto;
import kr.co.strato.portal.networking.v2.model.ServiceDto;
import kr.co.strato.portal.networking.v2.service.K8SServiceServiceV2;

@Api(tags = {"Network > Service V2"})
@RequestMapping("/api/v2/networking/service")
@RestController
public class K8SServiceControllerV2 extends CommonController {

	@Autowired
	private K8SServiceServiceV2 k8sServiceService;
	
	@GetMapping("/{clusterIdx}")
    public ResponseWrapper<List<ServiceDto>> getServiceList(@PathVariable(required = true) Long clusterIdx) throws Exception {
		List<ServiceDto> results = k8sServiceService.getList(clusterIdx);
        return new ResponseWrapper<>(results);
    }
	
	@PostMapping("")
    public ResponseWrapper<ServiceDto> getServiceDetail(@RequestBody NetworkCommonDto.Search search) throws Exception {
		ServiceDto detail = k8sServiceService.getDetail(search);
        return new ResponseWrapper<>(detail);
    }
	
	@PostMapping("/yaml")
    public ResponseWrapper<String> getServiceYaml(@RequestBody NetworkCommonDto.Search search) throws Exception {		
		String yaml = k8sServiceService.getYaml(search);        
        return new ResponseWrapper<>(yaml);
    }
	
	@DeleteMapping("")
    public ResponseWrapper<Boolean> deleteService(@RequestBody NetworkCommonDto.Search search) throws Exception {
		boolean result = k8sServiceService.delete(search);
        return new ResponseWrapper<>(result);
    }
}
