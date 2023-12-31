package kr.co.strato.domain.project.repository;

import java.util.List;

import kr.co.strato.domain.cluster.model.ClusterEntity;
import kr.co.strato.portal.project.model.ProjectClusterDto;

public interface ProjectClusterRepositoryCustom {

	public List<ProjectClusterDto> getProjectClusterList(Long projectId);
	
	public List<ClusterEntity> getProjectClusterListExceptUse(Long projectId);
	
	public List<ClusterEntity> getProjecClusterListByNotUsedClusters();
	
}