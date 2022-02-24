package kr.co.strato.portal.project.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import kr.co.strato.global.model.PageRequest;
import kr.co.strato.global.model.ResponseWrapper;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.project.model.ProjectRequestDto;
import kr.co.strato.portal.project.model.ProjectUserDto;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.project.service.PortalProjectService;

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
    public ResponseWrapper<Page<ProjectDto>> getProjectList(@RequestParam int page, @RequestParam int size, @RequestBody ProjectDto param) {
        
    	PageRequest pageable = new PageRequest();
    	pageable.setPage(page);
    	pageable.setSize(size);
    	
    	Page<ProjectDto> response = portalProjectService.getProjectList(pageable.of(), param);
        
        return new ResponseWrapper<Page<ProjectDto>>(response);
    }
    
    /**
     * Project 상세 조회
     * @param projectId
     * @return
     */
    @GetMapping("/api/v1/project/projects/{projectIdx}")
    @ResponseStatus(HttpStatus.OK)
    public ResponseWrapper<ProjectDto> getProjectList(@PathVariable("projectIdx") Long projectIdx) {
        
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
     * Project 생성
     * @param 
     * @return
     */
    @PostMapping("/api/v1/project/projects")
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseWrapper<Long> createProject(@RequestBody ProjectRequestDto param) throws Exception {
        
    	/*System.out.println("Project Name === " + param.getProjectName());
    	System.out.println("Description === " + param.getDescription());
    	
    	List<ProjectClusterDto> clusterList = param.getClusterList();
    	System.out.println("Cluster Size === " + clusterList.size());
    	for(ProjectClusterDto cluster : clusterList) {
    		System.out.println("Cluster === " + cluster.getClusterIdx());
    	}
    	
    	List<ProjectUserDto> userList = param.getUserList();
    	System.out.println("User Size === " + userList.size());
    	for(ProjectUserDto user : userList) {
    		System.out.println("User === " + user.getUserId());
    	}*/
    	
    	Long response = portalProjectService.createProject(param);
        
        return new ResponseWrapper<Long> (response);
    }
}
