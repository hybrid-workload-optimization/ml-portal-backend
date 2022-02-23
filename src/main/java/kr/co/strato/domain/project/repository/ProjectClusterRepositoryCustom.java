package kr.co.strato.domain.project.repository;

import java.util.List;

import kr.co.strato.portal.project.model.ProjectClusterDto;

public interface ProjectClusterRepositoryCustom {

	public List<ProjectClusterDto> getProjectClusterList(Long projectId);
}