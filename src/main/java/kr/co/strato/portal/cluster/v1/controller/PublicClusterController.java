package kr.co.strato.portal.cluster.v1.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.v1.model.ModifyArgDto;
import kr.co.strato.portal.cluster.v1.model.PublicClusterDto;
import kr.co.strato.portal.cluster.v1.model.ScaleArgDto;
import kr.co.strato.portal.cluster.v1.service.PublicClusterService;
import kr.co.strato.portal.common.controller.CommonController;

@RequestMapping("/api/v1/cluster/public")
@Api(tags = {"Public cluster 생성 / 삭제 / Scale 조정 / 노드풀 변경"})
@RestController
public class PublicClusterController extends CommonController {
	
	@Autowired
	private PublicClusterService publicClusterService;
	
	@ApiOperation(value="클러스터 생성.",
	notes=""
			+"***cloudProvider 유효값***\n"
			+"```\n"
			+"Azure\r\n"
			+"AWS\r\n"
			+"GCP\r\n"
			+"Naver\r\n"
			+"vSphere\r\n"
			+"```\n"
			+"***Response callback***\n"
			+"```\n"
			+"@Post/RequestBody\n"
			+ "{\n"
			+ "  \"clusterIdx\": 185,\n"
			+ "  \"clusterJobType\": \"CLUSTER_CREATE\",\n"
			+ "  \"status\": \"start/finish\",\n"
			+ "  \"result\": \"success/fail”,\n"
			+ "  \"message\": \"result가 fail일때 실패 이유\n"
			+ "}"
	)
	@PostMapping("/provisioning")
    public ResponseWrapper<ClusterEntity> provisioningCluster(@RequestHeader Map<String, Object> header, @RequestBody PublicClusterDto.Povisioning param) {
		ClusterEntity entity = publicClusterService.provisioningCluster(param, getLoginUser(), header);
		return new ResponseWrapper<>(entity);
	}
	
	@ApiOperation(value="클러스터 삭제.",
		notes=""
				+"***Response callback***\n"
				+"```\n"
				+"@Post/RequestBody\n"
				+ "{\n"
				+ "  \"clusterIdx\": 185,\n"
				+ "  \"clusterJobType\": \"CLUSTER_DELETE\",\n"
				+ "  \"status\": \"start/finish\",\n"
				+ "  \"result\": \"success/fail”,\n"
				+ "  \"message\": \"result가 fail일때 실패 이유\n"
				+ "}"
	)
	@DeleteMapping("/delete")
    public ResponseWrapper<Boolean> deleteCluster(@RequestHeader Map<String, Object> header, @RequestBody PublicClusterDto.Delete param) {
		boolean success = publicClusterService.deleteCluster(param, getLoginUser(), header);
		return new ResponseWrapper<>(success);
	}
	
	/**
	 * 클러스터 Scale 조정
	 */
	
	@ApiOperation(value="Scale 조정",
		notes=""
				+"***Response callback***\n"
				+"```\n"
				+"@Post/RequestBody\n"
				+ "{\n"
				+ "  \"clusterIdx\": 185,\n"
				+ "  \"clusterJobType\": \"CLUSTER_SCALE\",\n"
				+ "  \"status\": \"start/finish\",\n"
				+ "  \"result\": \"success/fail”,\n"
				+ "  \"message\": \"result가 fail일때 실패 이유\n"
				+ "}"
	)
	@PostMapping("/scale")
	public ResponseWrapper<String> scale(@RequestHeader Map<String, Object> header, @RequestBody ScaleArgDto scaleDto) {
		publicClusterService.scaleJobCluster(scaleDto, getLoginUser(), header);
		return new ResponseWrapper<>();
	}
	
	/**
	 * 클러스터 Scale 조정
	 */
	@ApiOperation(value="클러스터 노드풀 변경",
		notes=""
				+"***Response callback***\n"
				+"```\n"
				+"@Post/RequestBody\n"
				+ "{\n"
				+ "  \"clusterIdx\": 185,\n"
				+ "  \"clusterJobType\": \"CLUSTER_MODIFY\",\n"
				+ "  \"status\": \"start/finish\",\n"
				+ "  \"result\": \"success/fail”,\n"
				+ "  \"message\": \"result가 fail일때 실패 이유\n"
				+ "}"
	)
	@PostMapping("/modify")
	public ResponseWrapper<String> modify(@RequestHeader Map<String, Object> header, @RequestBody ModifyArgDto modifyDto) {
		publicClusterService.modifyJobCluster(modifyDto, getLoginUser(), header);
		return new ResponseWrapper<>();
	}
	
	@PostMapping("/install-addon/{clusterIdx}")
	@ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Boolean> installAddonPackage(@PathVariable Long clusterIdx) {
		boolean isOk = publicClusterService.instalAddonPackage(clusterIdx);
		return new ResponseWrapper<>(isOk);
	}
}
