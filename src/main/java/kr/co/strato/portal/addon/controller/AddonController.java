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
import kr.co.strato.portal.addon.model.Addon;
import kr.co.strato.portal.addon.service.AddonService;

@RestController
public class AddonController {

	@Autowired
	private AddonService addonService;

	
	@ApiOperation(value="Addon 리스트")
	@GetMapping("/api/v1/addon/list")
	public List<Addon> getAddons(@RequestParam Long clusterId) throws IOException {
		return addonService.getAddons(clusterId);
	}
	
	@ApiOperation(value="Addon 상세 조회")
	@GetMapping("/api/v1/addon/detail")
	public Addon getAddon(
			@RequestParam Long clusterId, 
			@RequestParam String addonId) {
		return addonService.getAddon(clusterId, addonId);
	}
	
	
	@ApiOperation(value="Addon 설치",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"kubeConfigId\": 1,\r\n"
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
	public boolean install(@RequestBody Map<String, Object> param) {
		Long clusterId = (Long)param.get("clusterId");
		String addonId = (String)param.get("addonId");
		Map<String, Object> parameters = null;
		if(param.get("parameters") != null) {
			parameters = (Map<String, Object>)param.get("parameters");
		}	
		return addonService.installAddon(clusterId, addonId, parameters);
	}
	
	@ApiOperation(value="Addon 삭제",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"kubeConfigId\": 1,\r\n"
			+ "  \"addonId\": \"1\"\r\n"
			+ "}\r\n"		
			+"```\n"
			+"***출력부***\n"
			+"```\n"
			+"true or false"
			+"\n"
	)
	@DeleteMapping("/api/v1/addon/uninstall")
	public boolean uninstall(@RequestBody Map<String, Object> param) {
		Long clusterId = (Long)param.get("clusterId");
		String addonId = (String)param.get("addonId");
		return addonService.uninstallAddon(clusterId, addonId);
	}
	
}
