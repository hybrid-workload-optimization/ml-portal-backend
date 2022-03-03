package kr.co.strato.domain.project.repository;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import kr.co.strato.portal.project.model.ProjectDto;

public interface ProjectRepositoryCustom {

	public PageImpl<ProjectDto> getProjectList(Pageable pageable, ProjectDto param);
	
	public ProjectDto getProjectDetail(Long projectIdx);
}