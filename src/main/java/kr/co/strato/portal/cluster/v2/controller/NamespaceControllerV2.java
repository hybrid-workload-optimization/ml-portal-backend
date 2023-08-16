package kr.co.strato.portal.cluster.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v2.model.NamespaceDto;
import kr.co.strato.portal.cluster.v2.service.NamespaceService;

@Api(tags = {"Namespace API V2"})
@RequestMapping("/api/v2/namespace")
@RestController
public class NamespaceControllerV2 {
	
	@Autowired
	private NamespaceService namespaceService;

	@Operation(summary = "Namespace 리스트 정보 조회", description = "Namespace 리스트를 조회한다.")
	@GetMapping("/{clusterIdx}/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<NamespaceDto.ListDto>> getNamespaceList(@PathVariable(required = true) Long clusterIdx) {
		List<NamespaceDto.ListDto> list = namespaceService.getList(clusterIdx);
        return new ResponseWrapper<>(list);
    }
	
	@Operation(summary = "Namespace 상세 정보 조회", description = "Namespace 상세 정보를 조회한다.")
	@GetMapping("/{clusterIdx}/{namespace}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<NamespaceDto.ListDto> getNamespaceDetail(
    		@PathVariable(required = true) Long clusterIdx,
    		@PathVariable(required = true) String namespace) {
		NamespaceDto.ListDto detail = namespaceService.getDetail(clusterIdx, namespace);
        return new ResponseWrapper<>(detail);
    }
}