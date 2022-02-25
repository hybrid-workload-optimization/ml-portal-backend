package kr.co.strato.portal.setting.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.ToolsDto;
import kr.co.strato.portal.setting.service.ToolsService;

@RestController
@RequestMapping("/api/v1/setting-tool")
public class ToolsController {
	
	@Autowired
	private ToolsService toolsService;
	
	@GetMapping("/tools")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<ToolsDto> getTools(ToolsDto params){
		ToolsDto tools = toolsService.getTools(params);
		return new ResponseWrapper<>(tools);
	}
	
	@PostMapping("/tools")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Long> postTools(@RequestBody ToolsDto params){
		Long l = toolsService.postTools(params);
		return new ResponseWrapper<>(l);
	}
	
	@PatchMapping("/tools")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Long> patchTools(@RequestBody ToolsDto params){
		Long l = toolsService.patchTools(params);
		return new ResponseWrapper<>(l);
	}
}
