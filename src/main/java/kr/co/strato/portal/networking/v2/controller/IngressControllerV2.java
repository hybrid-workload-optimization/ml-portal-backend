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
import kr.co.strato.portal.networking.v2.model.IngressDto;
import kr.co.strato.portal.networking.v2.model.NetworkCommonDto;
import kr.co.strato.portal.networking.v2.service.IngressServiceV2;

@Api(tags = {"Network > Ingress V2"})
@RequestMapping("/api/v2/networking/ingress")
@RestController
public class IngressControllerV2 extends CommonController {

	@Autowired
	private IngressServiceV2 ingressService;
	
	@GetMapping("/{clusterIdx}")
    public ResponseWrapper<List<IngressDto>> getIngressList(@PathVariable(required = true) Long clusterIdx) throws Exception {
		List<IngressDto> results = ingressService.getList(clusterIdx);
        return new ResponseWrapper<>(results);
    }
	
	@PostMapping("")
    public ResponseWrapper<IngressDto> getIngressDetail(@RequestBody NetworkCommonDto.Search search) throws Exception {
		IngressDto detail = ingressService.getDetail(search);
        return new ResponseWrapper<>(detail);
    }
	
	@PostMapping("/yaml")
    public ResponseWrapper<String> getIngressYaml(@RequestBody NetworkCommonDto.Search search) throws Exception {		
		String yaml = ingressService.getYaml(search);        
        return new ResponseWrapper<>(yaml);
    }
	
	@DeleteMapping("")
    public ResponseWrapper<Boolean> deleteIngress(@RequestBody NetworkCommonDto.Search search) throws Exception {
		boolean result = ingressService.delete(search);
        return new ResponseWrapper<>(result);
    }
}
