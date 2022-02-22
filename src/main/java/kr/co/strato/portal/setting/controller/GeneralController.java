package kr.co.strato.portal.setting.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.CommonType;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.model.GeneralDto;
import kr.co.strato.portal.setting.service.GeneralService;

@RestController
@RequestMapping("/api/v1/setting-general")
public class GeneralController {

	@Autowired
	GeneralService generalService;

	// 목록
	@GetMapping("/general-settings")
	public ResponseWrapper<List<GeneralDto>> getList() {
		ResponseWrapper<List<GeneralDto>> result = null;
		List<GeneralDto> generalSettings = generalService.getList();
		if (generalSettings != null) {
			result = new ResponseWrapper<List<GeneralDto>>(generalSettings);
		}
		return result;
	}

	// 상세
	@GetMapping("/general-settings/{idx}")
	public ResponseWrapper<GeneralDto> get(@PathVariable(name = "idx") Long idx) {
		ResponseWrapper<GeneralDto> result = null;
		GeneralDto generalSetting = generalService.get(idx);
		if (generalSetting != null) {
			result = new ResponseWrapper<GeneralDto>(generalSetting);
		}
		return result;
	}

	// 생성
	@PostMapping("/general-settings")
	public ResponseWrapper<GeneralDto> post(@RequestBody GeneralDto generalDto) {
		generalService.save(generalDto);
		ResponseWrapper<GeneralDto> result = new ResponseWrapper<GeneralDto>(CommonType.OK);
		return result;
	}

	// 수정
	@PutMapping("/general-settings/{idx}")
	public ResponseWrapper<GeneralDto> put(@PathVariable(name = "idx") Long idx, @RequestBody GeneralDto generalDto) {
		generalDto.setIdx(idx);
		generalService.save(generalDto);
		ResponseWrapper<GeneralDto> result = new ResponseWrapper<GeneralDto>(CommonType.OK);
		return result;
	}

}
