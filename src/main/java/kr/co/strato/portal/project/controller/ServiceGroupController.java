package kr.co.strato.portal.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.project.service.ServiceGroupService;

/**
 * PaaS Portal에서는 기존 Project에서 Service Group으로 변경되었기 때문에
 * 외부 Open API는 ServiceGroup라는 명칭으로 작성함
 * @author hclee
 *
 */
@RequestMapping("/api/v1/service-group")
@Api(tags = {"서비스 그룹 관리"})
@RestController
public class ServiceGroupController {
	
	@Autowired
	private ServiceGroupService serviceGroupService;

	@GetMapping("/{uuid}/clusters")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ClusterDto.Detail>> getGroupClusters(@PathVariable("uuid") String uuid) {        
    	List<ClusterDto.Detail> response = serviceGroupService.getGroupClusters(uuid);
        return new ResponseWrapper<List<ClusterDto.Detail>>(response);
    }
}
