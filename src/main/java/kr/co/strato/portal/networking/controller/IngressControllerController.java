package kr.co.strato.portal.networking.controller;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.networking.model.IngressControllerDto;
import kr.co.strato.portal.networking.service.IngressControllerService;

@RequestMapping("/api/v1/networking")
@RestController
public class IngressControllerController {
	
	@Autowired
	private IngressControllerService ingressControllerService;
	
	
	/**
	 * IngressController 이름 목록
	 * @param provider
	 * @return
	 */
	@GetMapping("/ingressController/names")
	public ResponseWrapper<String[]> getIngressControllerNames(@RequestParam String provider) {
		String[] types = ingressControllerService.types(provider);
		return new ResponseWrapper<>(types);
	}
	
	
	/**
	 * 리스트
	 * @param pageRequest
	 * @param clusterIdx
	 * @return
	 */
	@GetMapping("/ingressController/list")
	public ResponseWrapper<Page<IngressControllerDto.ResListDto>> getIngressControllerList(PageRequest pageRequest, Long clusterIdx) {
		Page<IngressControllerDto.ResListDto> list = ingressControllerService.getList(pageRequest.of(), clusterIdx);
		return new ResponseWrapper<>(list);
	}
	
	
	@ApiOperation(value="Ingress Controller 생성",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"		
			+ "  \"name\": \"nginx\",\r\n"
			+ "  \"serviceType\": \"NodePort\",\r\n"
			+ "  \"replicas\": 1,\r\n"
			+ "  \"isDefault\": true,\r\n"
			+ "  \"httpPort\": 30001,\r\n"
			+ "  \"httpsPort\": 30002\r\n"
			+ "}\r\n"
			+ "or\r\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"		
			+ "  \"name\": \"nginx\",\r\n"
			+ "  \"replicas\": 1,\r\n"
			+ "  \"serviceType\": \"ExternalIPs\",\r\n"
			+ "  \"isDefault\": true,\r\n"
			+ "  \"externalIPs\": [\r\n"
			+ "  	\"127.0.0.1\",\r\n"
			+ "  	\"127.0.0.2\"\r\n"
			+ "  ]\r\n"
			+ "}\r\n"
			+"```\n"
	)
	@PostMapping("/ingressController")
	public ResponseWrapper<Long> createIngressController(IngressControllerDto.ReqCreateDto param) throws IOException {
		Long id = ingressControllerService.create(param);
		return new ResponseWrapper<>(id);
	}
	
	
	@ApiOperation(value="Ingress Controller 수정",
	notes=""
			+"***입력부***\n"
			+"```\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"		
			+ "  \"name\": \"nginx\",\r\n"
			+ "  \"serviceType\": \"NodePort\",\r\n"
			+ "  \"replicas\": 1,\r\n"
			+ "  \"isDefault\": true,\r\n"
			+ "  \"httpPort\": 30001,\r\n"
			+ "  \"httpsPort\": 30002\r\n"
			+ "}\r\n"
			+ "or\r\n"
			+"{\r\n"
			+ "  \"clusterIdx\": 1,\r\n"		
			+ "  \"name\": \"nginx\",\r\n"
			+ "  \"replicas\": 1,\r\n"
			+ "  \"serviceType\": \"ExternalIPs\",\r\n"
			+ "  \"isDefault\": true,\r\n"
			+ "  \"externalIPs\": [\r\n"
			+ "  	\"127.0.0.1\",\r\n"
			+ "  	\"127.0.0.2\"\r\n"
			+ "  ]\r\n"
			+ "}\r\n"
			+"```\n"
	)
	@PutMapping("/ingressController")
	public ResponseWrapper<Long> updateIngressController(IngressControllerDto.ReqCreateDto param) throws IOException {
		Long id = ingressControllerService.update(param);
		return new ResponseWrapper<>(id);
	}
	
	
	/**
	 * 삭제
	 * @param param
	 * @return
	 */
	@DeleteMapping("/ingressController")
	public ResponseWrapper<Boolean> removeIngressController(@RequestBody IngressControllerDto.SearchParam param) {
		boolean isOk = ingressControllerService.remove(param);
		return new ResponseWrapper<>(isOk);
	}
	
}
