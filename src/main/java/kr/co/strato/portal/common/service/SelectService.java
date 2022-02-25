package kr.co.strato.portal.common.service;

import kr.co.strato.domain.project.model.ProjectEntity;
import kr.co.strato.domain.project.service.ProjectDomainService;
import kr.co.strato.portal.common.model.SelectProjectDto;
import kr.co.strato.portal.common.model.SelectProjectDtoMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SelectService {
    @Autowired
    private ProjectDomainService projectDomainService;

    public List<SelectProjectDto> getSelectProjects(){
        List<ProjectEntity> projects = projectDomainService.getProjects();
        List<SelectProjectDto> selectProjects =  projects.stream().map( e -> SelectProjectDtoMapper.INSTANCE.toDto(e)).collect(Collectors.toList());

        return selectProjects;
    }
}
