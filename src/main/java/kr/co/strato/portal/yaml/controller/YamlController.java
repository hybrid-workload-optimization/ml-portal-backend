package kr.co.strato.portal.yaml.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.yaml.model.YamlDto;
import kr.co.strato.portal.yaml.service.YamlService;

@Api(tags = {"Yaml API"})
@RequestMapping("/api/v1/yaml")
@RestController
public class YamlController {
	
	@Autowired
	private YamlService yamlService;
	
	@ApiOperation(value="Yaml 조회")
	@PostMapping("")
	public ResponseWrapper<List<YamlDto>> getYaml(@RequestBody YamlDto.Search param) {
		List<YamlDto> list = yamlService.getYaml(param);
		return new ResponseWrapper<List<YamlDto>>(list);
	}

	@ApiOperation(value="Yaml apply")
	@PostMapping("/apply")
	public ResponseWrapper<YamlDto.ApplyResultDto> apply(@RequestBody YamlDto.ApplyDto param) {
		YamlDto.ApplyResultDto r = yamlService.apply(param);
		return new ResponseWrapper<YamlDto.ApplyResultDto>(r);
	}
	
	@ApiOperation(value="Yaml 삭제")
	@DeleteMapping("{yamlIdx}")
	public ResponseWrapper<Boolean> deleteYaml(@PathVariable Long yamlIdx) {
		Boolean isDelete = yamlService.deleteYaml(yamlIdx);
		return new ResponseWrapper<Boolean>(isDelete);
	}
	
}
