package kr.co.strato.portal.setting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.GeneralDto;
import kr.co.strato.portal.setting.service.GeneralService;

@RestController
@RequestMapping("/api/v1/setting/generals")
public class GeneralController {
	
	@Autowired
	GeneralService generalService;
	//목록
	@GetMapping("/")
	public ResponseWrapper<List<GeneralDto>> getList(){
		generalService.getList();
		return null;
	}
}
