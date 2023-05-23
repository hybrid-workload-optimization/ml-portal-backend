package kr.co.strato.portal.csp.vsphere.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.csp.vsphere.service.VSphereConfigurationService;

@RequestMapping("/api/v1/csp/vsphere")
@RestController
public class VSphereConfigurationController {

	@Autowired
	private VSphereConfigurationService vsphereService;
	
	@GetMapping("/templates/{cspAccountUuid}")
	public ResponseWrapper<List<String>> getTemplates(@PathVariable String cspAccountUuid) {		
		List<String> list = vsphereService.getTemplates(cspAccountUuid);
		return new ResponseWrapper<>(list);
	}	
	
}
