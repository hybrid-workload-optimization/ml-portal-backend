package kr.co.strato.domain.machineLearning.repository;

import java.util.List;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import kr.co.strato.domain.machineLearning.model.MLEntity;
import kr.co.strato.portal.ml.model.MLProjectDto;
import kr.co.strato.portal.project.model.ProjectDto;
import kr.co.strato.portal.setting.model.UserDto;

public interface CustomMLProjectRepository {

	public PageImpl<MLProjectDto> getProjectList(UserDto loginUser, Pageable pageable, String projectName) throws Exception;
	public MLProjectDto getProjectDetail(Long projectIdx);
	public List<MLEntity> getProjectMlList(Long projectIdx);
}
