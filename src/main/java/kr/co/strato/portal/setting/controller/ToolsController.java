package kr.co.strato.portal.setting.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;

@RestController
@RequestMapping("/api/v1/tools")
public class ToolsController {

	@GetMapping("/{type}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> getTools(@PathVariable(name = "type") String type){
		return new ResponseWrapper<>(null);
	}
	
	@PatchMapping("/{type}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<String> patchTools(@PathVariable(name = "type") String type){
		return new ResponseWrapper<>(null);
	}
}
