package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.portal.project.model.ProjectClusterDto;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectClusterDomainService {

	@Autowired
	ProjectClusterRepository projectClusterRepository;
	
	/**
     * Project의 Cluster 리스트 조회
     * @param projectIdx
     * @return
     */
	public List<ProjectClusterDto> getProjectClusterList(Long projectIdx) {
		
		return projectClusterRepository.getProjectClusterList(projectIdx);
	}
}
