package kr.co.strato.portal.cluster.v2.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v2.model.StorageClassDto;
import kr.co.strato.portal.cluster.v2.service.StorageClassService;

@Api(tags = {"Cluster > StorageClass V2"})
@RequestMapping("/api/v2/storageClass")
@RestController
public class StorageClassControllerV2 {
	
	@Autowired
	private StorageClassService storageService;

	@Operation(summary = "StorageClass 리스트 정보 조회", description = "StorageClass 리스트를 조회한다.")
	@GetMapping("/{clusterIdx}/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<StorageClassDto.ListDto>> getStorageClassList(@PathVariable(required = true) Long clusterIdx) {
		List<StorageClassDto.ListDto> list = storageService.getList(clusterIdx);
        return new ResponseWrapper<>(list);
    }
	
	@Operation(summary = "StorageClass 상세 정보 조회", description = "StorageClass 상세 정보를 조회한다.")
	@GetMapping("/{clusterIdx}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<StorageClassDto.ListDto> getStorageClassDetail(
    		@PathVariable(required = true) Long clusterIdx,
    		@PathVariable(required = true) String name) {
		StorageClassDto.ListDto detail = storageService.getDetail(clusterIdx, name);
        return new ResponseWrapper<>(detail);
    }
	
	@Operation(summary = "StorageClass 삭제", description = "StorageClass를 삭제한다.")
	@DeleteMapping("")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deleteStorageClass(@RequestBody StorageClassDto.DeleteDto param) {
		boolean isDeleted = storageService.delete(param);
		return new ResponseWrapper<>(isDeleted);
	}
}
