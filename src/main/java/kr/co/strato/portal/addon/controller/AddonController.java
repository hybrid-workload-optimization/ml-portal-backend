package kr.co.strato.portal.addon.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.service.AddonService;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.setting.model.UserDto;

@RestController
public class AddonController extends CommonController {

	@Autowired
	private AddonService addonService;

	
	@ApiOperation(value="Addon 리스트")
	@GetMapping("/api/v1/addon/list")
	public ResponseWrapper<List<Addon>> getAddons(@RequestParam Long clusterIdx) throws IOException {
		return new ResponseWrapper<>(addonService.getAddons(clusterIdx));
	}
	
	@ApiOperation(value="Addon 상세 조회")
	@GetMapping("/api/v1/addon/detail")
	public ResponseWrapper<Addon> getAddon(
			@RequestParam Long clusterIdx, 
			@RequestParam String addonId) {
		return new ResponseWrapper<>(addonService.getAddon(clusterIdx, addonId));
	}
	
	
	@ApiOperation(value="Addon 설치",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"
			+ "  \"addonId\": \"1\",\r\n"
			+ "  \"parameters\":  {\r\n"
			+ "  }\r\n"
			+ "}\r\n"		
			+"```\n"
			+"***출력부***\n"
			+"```\n"
			+"true or false"
			+"\n"
	)
	@PostMapping("/api/v1/addon/install")
	public ResponseWrapper<Boolean> install(@RequestBody Map<String, Object> param) {
		Long clusterIdx = Long.valueOf((int)param.get("clusterIdx"));
		String addonId = (String)param.get("addonId");
		Map<String, Object> parameters = null;
		if(param.get("parameters") != null) {
			parameters = (Map<String, Object>)param.get("parameters");
		}
		
		String userId = null;
		UserDto user = getLoginUser();
		if(user != null) {
			userId = user.getUserId();
		}
		
		return new ResponseWrapper<>(addonService.installAddon(clusterIdx, addonId, parameters, userId));
	}
	
	@ApiOperation(value="Addon 삭제",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"
			+ "  \"addonId\": \"1\"\r\n"
			+ "}\r\n"		
			+"```\n"
			+"***출력부***\n"
			+"```\n"
			+"true or false"
			+"\n"
	)
	@DeleteMapping("/api/v1/addon/uninstall")
	public ResponseWrapper<Boolean> uninstall(@RequestBody Map<String, Object> param) {
		Long clusterIdx = Long.valueOf((int)param.get("clusterIdx"));
		String addonId = (String)param.get("addonId");
		return new ResponseWrapper<>(addonService.uninstallAddon(clusterIdx, addonId));
	}
	
	@ApiOperation(value="Addon 설치 여부 문의.",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"
			+ "  \"addonType\": \"cluster-monitoring\"\r\n"
			+ "}\r\n"		
			+"```\n"
			+"***출력부***\n"
			+"```\n"
			+"true or false"
			+"\n"
	)
	@PostMapping("/api/v1/addon/isInstall")
	public ResponseWrapper<Boolean> isInstall(@RequestBody Map<String, Object> param) {
		Long clusterIdx = Long.valueOf((int)param.get("clusterIdx"));
		String addonType = (String)param.get("addonType");
		return new ResponseWrapper<>(addonService.isInstall(clusterIdx, addonType));
	}
	
	@ApiOperation(value="Addon 설치 여부 문의.",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"
			+ "  \"addonType\": \"cluster-monitoring\"\r\n"
			+ "}\r\n"		
			+"```\n"
			+"***출력부***\n"
			+"```\n"
			+"true or false"
			+"\n"
	)
	@PostMapping("/api/v1/addon/detailByType")
	public ResponseWrapper<Addon> getAddonByType(@RequestBody Map<String, Object> param) {
		Long clusterIdx = Long.valueOf((int)param.get("clusterIdx"));
		String addonType = (String)param.get("addonType");
		return new ResponseWrapper<>(addonService.getAddonByType(clusterIdx, addonType));
	}
		
}
