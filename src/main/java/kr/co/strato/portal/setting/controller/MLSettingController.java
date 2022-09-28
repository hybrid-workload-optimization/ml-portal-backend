package kr.co.strato.portal.setting.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.setting.service.MLSettingService;

@RestController
@RequestMapping("/api/v1/ml-setting")
public class MLSettingController {

	@Autowired
	MLSettingService mlSettingService;

	// 클라우드 제공자 조회.
	@GetMapping("/cloud-provider")
	public ResponseWrapper<String> getCloudProvider() {
		String cloudProvider = mlSettingService.getCloudProvider();		
		return new ResponseWrapper<String>(cloudProvider);
	}

	// 클라우드 제공자 설정.
	@PostMapping("/cloud-provider")
	public ResponseWrapper<Void> postGeneralSetting(@RequestBody Map<String, String> param) {
		String cloudProvider = param.get("cloudProvider");
		mlSettingService.setCloudProvider(cloudProvider);
		return new ResponseWrapper<Void>();
	}

}
