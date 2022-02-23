package kr.co.strato.domain.project.repository;

import java.util.List;

import org.springframework.data.domain.Page;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.model.ProjectUserEntity;
import kr.co.strato.portal.project.model.ProjectUserDto;

public interface ProjectUserRepositoryCustom {

	public List<ProjectUserDto> getProjectByUserId(String userId);
}
