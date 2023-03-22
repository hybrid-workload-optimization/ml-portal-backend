package kr.co.strato.domain.project.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.domain.cluster.repository.ClusterRepository;
import kr.co.strato.domain.project.model.ProjectClusterEntity;
import kr.co.strato.domain.project.repository.ProjectClusterRepository;
import kr.co.strato.portal.project.model.ProjectClusterDto;
import kr.co.strato.portal.setting.model.UserDto;

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
     * 프로젝트로 등록하지 않은 Cluster 리스트 조회
     * @param loginId
     * @return
     */
	public List<ClusterEntity> getProjecClusterListByNotUsedClusters(UserDto loginUser) {
		
		return projectClusterRepository.getProjecClusterListByNotUsedClusters(loginUser);
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
	
	/**
     * Project Cluster 삭제
     * @param projectIdx
     * @return
     */
	public Integer deleteProjectByProjectIdx(Long projectIdx) {
		
		return projectClusterRepository.deleteByProjectIdx(projectIdx);
	}
	
	/**
     * 해당 Cluster를 사용하는 Project 조회(현재 Project 제외)
     * @param projectIdx
     * @param clusterIdx
     * @return
     */
	public List<ProjectClusterEntity> getUseProjectByCluster(Long clusterIdx, Long projectIdx) {
		
		return projectClusterRepository.findByClusterIdxAndProjectIdxNot(clusterIdx, projectIdx);
	}
	
	/**
     * Project Cluster 삭제
     * @param projectIdx
     * @return
     */
	public Integer deleteProjectCluster(Long projectIdx, Long clusterIdx) {
		
		return projectClusterRepository.deleteByProjectIdxAndClusterIdx(projectIdx, clusterIdx);
	}
	
	/**
     * Project의 Cluster 조회
     * @param projectIdx
     * @param clusterIdx
     * @return
     */
	public ProjectClusterEntity getProjectCluster(Long projectIdx, Long clusterIdx) {
		
		return projectClusterRepository.findByProjectIdxAndClusterIdx(projectIdx, clusterIdx);
	}
	
	/**
     * Project Cluster 삭제
     * @param projectIdx
     * @return
     */
	public Integer deleteProjectClusterNotDuplicate(Long projectIdx) {
		
		return projectClusterRepository.deleteByProjectIdx(projectIdx);
	}
	
	/**
     * Project Cluster 삭제(삭제요청한 Cluster 삭제)
     * @param projectIdx
     * @return
     */
	public Integer deleteRequestProjectCluster(Long projectIdx, List<Long> clusters) {
		
		return projectClusterRepository.deleteByProjectIdxAndClusterIdxNotIn(projectIdx, clusters);
	}
	
	public List<ProjectClusterEntity> getProjectClusters(Long projectIdx) {
		return projectClusterRepository.findByProjectIdx(projectIdx);
	}
}
