package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.setting.model.UserDto;

public interface ProjectRepositoryCustom {

	public PageImpl<ProjectDto> getProjectList(Pageable pageable, ProjectDto param) throws Exception;
	
	public ProjectDto getProjectDetail(Long projectIdx, String type);
	
	public ProjectEntity getProjectDetailByClusterId(Long clusterIdx);
	
	/**
	 * 로그인한 사용자가 속한 프로젝트 반환.
	 * @param loginUser
	 * @return
	 */
	public List<ProjectEntity> getUserProjects(UserDto loginUser);
}