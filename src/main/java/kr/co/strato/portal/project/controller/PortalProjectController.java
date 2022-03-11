package kr.co.strato.portal.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.service.PortalProjectService;
import kr.co.strato.portal.setting.model.UserDto;

@RestController
public class PortalProjectController {

	@Autowired
	PortalProjectService portalProjectService;
	
	/**
     * Project 리스트 조회
     * @param pageRequest
     * @return
     */
    @GetMapping("/api/v1/project/projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Page<ProjectDto>> getProjectList(PageRequest pageRequest, ProjectDto param) {
        
    	Page<ProjectDto> response = portalProjectService.getProjectList(pageRequest.of(), param);
        
        return new ResponseWrapper<Page<ProjectDto>>(response);
    }
    
    /**
     * Project 상세 조회
     * @param projectId
     * @return
     */
    @GetMapping("/api/v1/project/projects/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<ProjectDto> getProjectDetail(@PathVariable("projectIdx") Long projectIdx) {
        
    	ProjectDto response = portalProjectService.getProjectDetail(projectIdx);
        
        return new ResponseWrapper<ProjectDto>(response);
    }
    
    /**
     * Project의 Cluster 리스트 조회
     * @param projectIdx
     * @return
     */
    @GetMapping("/api/v1/project/cluster/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ProjectClusterDto>> getProjectClusterList(@PathVariable("projectIdx") Long projectIdx) {
        
    	List<ProjectClusterDto> response = portalProjectService.getProjectClusterList(projectIdx);
        
        return new ResponseWrapper<List<ProjectClusterDto>> (response);
    }
    
    /**
     * Project의 User 리스트 조회
     * @param projectIdx
     * @return
     */
    @GetMapping("/api/v1/project/user/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ProjectUserDto>> getProjectUserList(@PathVariable("projectIdx") Long projectIdx) {
        
    	List<ProjectUserDto> response = portalProjectService.getProjectUserList(projectIdx);
        
        return new ResponseWrapper<List<ProjectUserDto>> (response);
    }
    
    /**
     * 로그인한 사용자가 생성한 Cluster 리스트 조회
     * @param 
     * @return
     */
    @GetMapping("/api/v1/project/clusters")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ClusterDto>> getProjecClusterListByCreateUserId(@RequestBody ProjectRequestDto param) {
        
    	List<ClusterDto> response = portalProjectService.getProjecClusterListByCreateUserId(param.getLoginId());
        
        return new ResponseWrapper<List<ClusterDto>>(response);
    }
    
    /**
     * 현재 사용중인 전체 User 리스트 조회
     * @param 
     * @return
     */
    @GetMapping("/api/v1/project/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<UserDto>> getProjecUserListByUseYn() throws Exception {
        
    	List<UserDto> response = portalProjectService.getProjecUserListByUseYn("Y");
        
        return new ResponseWrapper<List<UserDto>>(response);
    }
    
    /**
     * Project 생성
     * @param 
     * @return
     */
    @PostMapping("/api/v1/project/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> createProject(@RequestBody ProjectRequestDto param) throws Exception {
        
    	Long response = portalProjectService.createProject(param);
        
        return new ResponseWrapper<Long>(response);
    }
    
    /**
     * Project에서 사용중인 Cluster를 제외한 리스트 조회
     * @param 
     * @return
     */
    @GetMapping("/api/v1/project/{projectIdx}/clusters")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<ClusterDto>> getProjectClusterListExceptUse(@PathVariable("projectIdx") Long projectIdx) {
        
    	List<ClusterDto> response = portalProjectService.getProjectClusterListExceptUse(projectIdx);
        
        return new ResponseWrapper<List<ClusterDto>>(response);
    }
    
    /**
     * Project에서 사용중인 User를 제외한 리스트 조회
     * @param 
     * @return
     */
    @GetMapping("/api/v1/project/{projectIdx}/users")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<List<UserDto>> getProjectUserListExceptUse(@PathVariable("projectIdx") Long projectIdx) {
        
    	List<UserDto> response = portalProjectService.getProjectUserListExceptUse(projectIdx);
        
        return new ResponseWrapper<List<UserDto>>(response);
    }
    
    /**
     * Project 상세 정보 수정
     * @param 
     * @return
     */
    @PatchMapping("/api/v1/project/projects")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseWrapper<Boolean> updateProject(@RequestBody ProjectRequestDto param) {
        
    	Boolean response = portalProjectService.updateProject(param);
        
        return new ResponseWrapper<Boolean>(response);
    }
    
    /**
     * Project Cluster 수정
     * @param 
     * @return
     */
    @PatchMapping("/api/v1/project/cluster")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseWrapper<Boolean> updateProjectCluster(@RequestBody ProjectRequestDto param) {
        
    	Boolean response = portalProjectService.updateProjectCluster(param);
        
        return new ResponseWrapper<Boolean>(response);
    }
    
    /**
     * Project User 수정
     * @param 
     * @return
     */
    @PatchMapping("/api/v1/project/user")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public ResponseWrapper<Boolean> updateProjectUser(@RequestBody ProjectRequestDto param) {
        
    	Boolean response = portalProjectService.updateProjectUser(param);
        
        return new ResponseWrapper<Boolean>(response);
    }
    
    /**
     * Project 삭제
     * @param 
     * @return
     */
    @DeleteMapping("/api/v1/project/projects")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<Boolean> deleteProject(@RequestBody ProjectRequestDto param) {
        
    	Boolean response = portalProjectService.deleteProject(param);
        
        return new ResponseWrapper<Boolean>(response);
    }
}
