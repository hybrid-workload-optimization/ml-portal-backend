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
import kr.co.strato.portal.cluster.v2.model.PersistentVolumeDto;
import kr.co.strato.portal.cluster.v2.service.PersistentVolumeService;

@Api(tags = {"Cluster > PersistentVolume V2"})
@RequestMapping("/api/v2/pv")
@RestController
public class PersistentVolumeControllerV2 {
	
	@Autowired
	private PersistentVolumeService pvService;

	@Operation(summary = "PersistentVolume 리스트 정보 조회", description = "PersistentVolume 리스트를 조회한다.")
	@GetMapping("/{clusterIdx}/list")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<PersistentVolumeDto.ListDto>> getPersistentVolumeList(@PathVariable(required = true) Long clusterIdx) {
		List<PersistentVolumeDto.ListDto> list = pvService.getList(clusterIdx);
        return new ResponseWrapper<>(list);
    }
	
	@Operation(summary = "PersistentVolume 상세 정보 조회", description = "PersistentVolume 상세 정보를 조회한다.")
	@GetMapping("/{clusterIdx}/{name}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<PersistentVolumeDto.ListDto> getPersistentVolumeDetail(
    		@PathVariable(required = true) Long clusterIdx,
    		@PathVariable(required = true) String name) {
		PersistentVolumeDto.ListDto detail = pvService.getDetail(clusterIdx, name);
        return new ResponseWrapper<>(detail);
    }
	
	@Operation(summary = "PersistentVolume 삭제", description = "PersistentVolume을 삭제한다.")
	@DeleteMapping("")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> deletePersistentVolume(@RequestBody PersistentVolumeDto.DeleteDto param) {
		boolean isDeleted = pvService.delete(param);
		return new ResponseWrapper<>(isDeleted);
	}
}
