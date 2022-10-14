package kr.co.strato.portal.cluster.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.v3.oas.annotations.Operation;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ModifyArgDto;
import kr.co.strato.portal.cluster.model.PublicClusterDto;
import kr.co.strato.portal.cluster.model.ScaleArgDto;
import kr.co.strato.portal.cluster.service.PublicClusterService;
import kr.co.strato.portal.common.controller.CommonController;

@RequestMapping("/api/v1/cluster/public")
@Api(tags = {"Public cluster 생성 / 삭제 / Scale 조정 / 노드풀 변경"})
@RestController
public class PublicClusterController extends CommonController {
	
	@Autowired
	private PublicClusterService publicClusterService;
	
	@Operation(summary = "클러스터 생성", description = "Public cluster(AKS, GKE, EKS, Naver) 생성")
	@ApiOperation(value="리스트 요청.",
	notes=""
			+"***cloudProvider 유효값***\n"
			+"```\n"
			+"Azure\r\n"
			+"AWS\r\n"
			+"GCP\r\n"
			+"Naver\r\n"
			+"```\n"
	)
	@PostMapping("/provisioning")
    public ResponseWrapper<ClusterEntity> provisioningCluster(@RequestBody PublicClusterDto.Povisioning param) {
		ClusterEntity entity = publicClusterService.provisioningCluster(param, getLoginUser());
		return new ResponseWrapper<>(entity);
	}
	
	@Operation(summary = "클러스터 삭제", description = "Public cluster 삭제")
	@DeleteMapping("/delete")
    public ResponseWrapper<Boolean> deleteCluster(@RequestBody PublicClusterDto.Delete param) {
		boolean success = publicClusterService.deleteCluster(param, getLoginUser());
		return new ResponseWrapper<>(success);
	}
	
	/**
	 * 클러스터 Scale 조정
	 */
	@Operation(summary = "Scale 조정", description = "Cluster Scale 조정(Scale-In, Scale-Out)")
	@PostMapping("/scale")
	public ResponseWrapper<String> scale(@RequestBody ScaleArgDto scaleDto) {
		publicClusterService.scaleJobCluster(scaleDto, getLoginUser());
		return new ResponseWrapper<>();
	}
	
	/**
	 * 클러스터 Scale 조정
	 */
	@Operation(summary = "클러스터 노드풀 변경", description = "클러스터 노드풀 변경")
	@PostMapping("/modify")
	public ResponseWrapper<String> modify(@RequestBody ModifyArgDto modifyDto) {
		publicClusterService.modifyJobCluster(modifyDto, getLoginUser());
		return new ResponseWrapper<>();
	}

}
