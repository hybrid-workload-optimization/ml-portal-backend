package kr.co.strato.portal.machineLearning.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.common.controller.CommonController;
import kr.co.strato.portal.machineLearning.model.MLDto;
import kr.co.strato.portal.machineLearning.model.MLProjectDto;
import kr.co.strato.portal.machineLearning.service.MLProjectService;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.service.PortalProjectService;
import kr.co.strato.portal.setting.model.UserDto;

@RequestMapping("/api/v1/ml/project")
@RestController
public class MLProjectController extends CommonController {
	
	@Autowired
	private MLProjectService mlProjectService;
	
	@Autowired
	private PortalProjectService portalProjectService;

	/**
	 * ML 프로젝트 리스트 조회
	 * @param pageable
	 * @param param
	 * @return
	 * @throws Exception
	 */
	@GetMapping("/projects")
    @ResponseStatus(HttpStatus.OK)
	public ResponseWrapper<Page<MLProjectDto>> getList(Pageable pageable, ProjectDto param) throws Exception {
		UserDto loginUser = getLoginUser();
		Page<MLProjectDto> list = mlProjectService.getList(loginUser, pageable, param);
		return new ResponseWrapper<>(list);
	}
	
	
	/**
     * ML 프로젝트 상세 조회
     * @param projectId
     * @return
     */
    @GetMapping("/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<MLProjectDto> getProjectDetail(@PathVariable("projectIdx") Long projectIdx) {  
    	MLProjectDto projectDetail = mlProjectService.getProjectDetail(projectIdx);
        return new ResponseWrapper<MLProjectDto>(projectDetail);
    }
	
	
    /**
     * ML 프로젝트의 ML 리스트 조회
     * @param projectIdx
     * @return
     */
    @GetMapping("/ml-list/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<MLDto.Detail>> getProjectMlList(@PathVariable("projectIdx") Long projectIdx) {
    	List<MLDto.Detail> list = mlProjectService.getProjectMlList(projectIdx);
        return new ResponseWrapper<List<MLDto.Detail>> (list);
    }
    
    /**
     * ML 상세 조회
     * @param projectIdx
     * @return
     */
    @GetMapping("/ml/{mlIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<MLDto.Detail> getMlDetail(@PathVariable("mlIdx") Long mlIdx) {
    	MLDto.Detail detail = mlProjectService.getMl(mlIdx);
        return new ResponseWrapper<MLDto.Detail> (detail);
    }
    
    /**
     * Project의 Cluster 리스트 조회
     * @param projectIdx
     * @return
     */
    @GetMapping("/cluster/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ProjectClusterDto>> getProjectClusterList(@PathVariable("projectIdx") Long projectIdx) {
    	List<ProjectClusterDto> response = portalProjectService.getProjectClusterList(projectIdx);        
        return new ResponseWrapper<List<ProjectClusterDto>> (response);
    }
    
    /**
     * Project의 User 리스트 조회(Project Manager 제외)
     * @param projectIdx
     * @return
     */
    @GetMapping("/user/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ProjectUserDto>> getProjectUserListExceptManager(@PathVariable("projectIdx") Long projectIdx) {        
    	List<ProjectUserDto> response = portalProjectService.getProjectUserListExceptManager(projectIdx);        
        return new ResponseWrapper<List<ProjectUserDto>> (response);
    }
	
}
