package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.domain.project.repository.ProjectUserRepository;
import kr.co.strato.portal.cluster.model.ClusterDto;
import kr.co.strato.portal.project.model.ProjectClusterDto;

@Service
@Transactional(rollbackFor = Exception.class)
public class ProjectClusterDomainService {

	@Autowired
	ProjectClusterRepository projectClusterRepository;
	
	@Autowired
	ClusterRepository clusterRepository;
	
	/**
     * Project의 Cluster 리스트 조회
     * @param projectIdx
     * @return
     */
	public List<ProjectClusterDto> getProjectClusterList(Long projectIdx) {
		
		return projectClusterRepository.getProjectClusterList(projectIdx);
	}
	
	/**
     * 로그인한 사용자가 생성한 Cluster 리스트 조회
     * @param loginId
     * @return
     */
	public List<ClusterEntity> getProjecClusterListByCreateUserId(String loginId) {
		
		return clusterRepository.findByCreateUserId(loginId);
	}
	
	/**
     * Project에서 사용중인 Cluster를 제외한 리스트 조회
     * @param projectIdx
     * @return
     */
	public List<ClusterEntity> getProjectClusterListExceptUse(Long projectIdx) {
		
		return projectClusterRepository.getProjectClusterListExceptUse(projectIdx);
	}
	
	/**
     * Project Cluster 생성
     * @param entity
     * @return
     */
	public ProjectClusterEntity createProjectCluster(ProjectClusterEntity entity) {
		
		return projectClusterRepository.save(entity);
	}
}
