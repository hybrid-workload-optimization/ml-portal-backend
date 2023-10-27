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
import kr.co.strato.portal.config.v2.model.SecretDto;
import kr.co.strato.portal.config.v2.service.SecretServiceV2;

@Api(tags = {"Config > Secret V2"})
@RequestMapping("/api/v2/config/secret")
@RestController
public class SecretControllerV2 extends CommonController {

	@Autowired
	private SecretServiceV2 secretService;
	
	@GetMapping("/{clusterIdx}")
    public ResponseWrapper<List<SecretDto>> getSecretList(@PathVariable(required = true) Long clusterIdx) throws Exception {
		List<SecretDto> results = secretService.getList(clusterIdx);
        return new ResponseWrapper<>(results);
    }
	
	@PostMapping("")
    public ResponseWrapper<SecretDto> getSecretDetail(@RequestBody ConfigCommonDto.Search search) throws Exception {
		SecretDto detail = secretService.getDetail(search);
        return new ResponseWrapper<>(detail);
    }
	
	@PostMapping("/yaml")
    public ResponseWrapper<String> getSecretYaml(@RequestBody ConfigCommonDto.Search search) throws Exception {		
		String yaml = secretService.getYaml(search);        
        return new ResponseWrapper<>(yaml);
    }
	
	@DeleteMapping("")
    public ResponseWrapper<Boolean> deleteSecret(@RequestBody ConfigCommonDto.Search search) throws Exception {
		boolean result = secretService.delete(search);
        return new ResponseWrapper<>(result);
    }
}
