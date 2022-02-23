package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.domain.Pageable;

import kr.co.strato.portal.project.model.ProjectDto;

public interface ProjectRepositoryCustom {

	public List<ProjectDto> getProjectList(Pageable pageable, ProjectDto param);
	
	public ProjectDto getProjectDetail(Long projectId);
}