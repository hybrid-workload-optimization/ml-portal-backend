package kr.co.strato.portal.networking.v1.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.ApiOperation;
import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.networking.v1.model.IngressControllerDto;
import kr.co.strato.portal.networking.v1.service.IngressControllerService;

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
	@GetMapping("/ingressController/{clusterIdx}/names")
	public ResponseWrapper<List<String>> getIngressControllerNames(
			@PathVariable Long clusterIdx) {
		List<String> types = ingressControllerService.types(clusterIdx);
		return new ResponseWrapper<>(types);
	}
	
	
	@GetMapping("/ingressController/{clusterIdx}/existDefaultController")
	public ResponseWrapper<Boolean> isExistDefaultController(
			@PathVariable Long clusterIdx) {
		boolean isExist = ingressControllerService.isExistDefaultController(clusterIdx);
		return new ResponseWrapper<>(isExist);
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
	
	
	@ApiOperation(value="Public Cloud - Ingress Controller 생성(현재 Azure만 지원함.)")
	@PostMapping("/ingressController/{clusterIdx}/create") 
	public ResponseWrapper<Long> createIngressController(@PathVariable Long clusterIdx) throws IOException {
		Long id = ingressControllerService.create(clusterIdx);
		return new ResponseWrapper<>(id);
	}
	
}
