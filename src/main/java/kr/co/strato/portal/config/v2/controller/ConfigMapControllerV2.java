package kr.co.strato.portal.config.v2.controller;

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
import kr.co.strato.portal.config.v2.model.ConfigCommonDto;
import kr.co.strato.portal.config.v2.model.ConfigMapDto;
import kr.co.strato.portal.config.v2.service.ConfigMapServiceV2;

@Api(tags = {"Config > ConfigMap V2"})
@RequestMapping("/api/v2/config/configMap")
@RestController
public class ConfigMapControllerV2 extends CommonController {

	@Autowired
	private ConfigMapServiceV2 configMapService;
	
	@GetMapping("/{clusterIdx}")
    public ResponseWrapper<List<ConfigMapDto>> getConfigMapList(@PathVariable(required = true) Long clusterIdx) throws Exception {
		List<ConfigMapDto> results = configMapService.getList(clusterIdx);
        return new ResponseWrapper<>(results);
    }
	
	@PostMapping("")
    public ResponseWrapper<ConfigMapDto> getConfigMapDetail(@RequestBody ConfigCommonDto.Search search) throws Exception {
		ConfigMapDto detail = configMapService.getDetail(search);
        return new ResponseWrapper<>(detail);
    }
	
	@PostMapping("/yaml")
    public ResponseWrapper<String> getConfigMapYaml(@RequestBody ConfigCommonDto.Search search) throws Exception {		
		String yaml = configMapService.getYaml(search);        
        return new ResponseWrapper<>(yaml);
    }
	
	@DeleteMapping("")
    public ResponseWrapper<Boolean> deleteConfigMap(@RequestBody ConfigCommonDto.Search search) throws Exception {
		boolean result = configMapService.delete(search);
        return new ResponseWrapper<>(result);
    }
}
